package ui;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import data.DataStore;
import model.Account;
import model.Product;
import model.Sale;

/*
 * Admin terminal, shown after an admin account logs in (Account.isAdmin()).
 * Two modules from the brief:
 *   PRODUCTS & STOCK : add / edit / delete products and their stock (CRUD)
 *   DAILY SALES      : today's completed sales and totals
 * Everything reads/writes the in-memory DataStore, so changes show up in the
 * cashier's store immediately.
 *
 * The products table rows mirror DataStore.products 1:1 and in order, so a
 * selected row is also its index in the list. Sorting is left off to keep that
 * true; add sorting only together with a row->product lookup.
 */
public class AdminFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String[] SECTIONS = {"MEN", "WOMEN", "KIDS", "IT"};
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Account admin;
    private final DefaultTableModel productModel =
            new DefaultTableModel(new String[]{"ID", "NAME", "CATEGORY", "SECTION", "PRICE", "STOCK"}, 0) {
                private static final long serialVersionUID = 1L;
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable productTable = new JTable(productModel);

    private final DefaultTableModel salesModel =
            new DefaultTableModel(new String[]{"RECEIPT", "TIME", "CASHIER", "ITEMS", "TOTAL"}, 0) {
                private static final long serialVersionUID = 1L;
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
    private final JTable salesTable = new JTable(salesModel);
    private final JLabel lblSummary = UITheme.label("", Font.BOLD, 13f, 0.05f, UITheme.BLACK);

    public AdminFrame(Account admin) {
        this.admin = admin;

        setTitle("DAMNIT - Admin");
        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);
        root.add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.font(Font.BOLD, 12f, 0.1f));
        tabs.setBackground(UITheme.BG);
        tabs.addTab("PRODUCTS & STOCK", buildProductsTab());
        tabs.addTab("DAILY SALES", buildSalesTab());
        tabs.setBorder(BorderFactory.createEmptyBorder(16, 40, 24, 40));
        root.add(tabs, BorderLayout.CENTER);

        refreshProducts();
        refreshSales();
    }

    // ---------- Header ----------

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel strip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 3));
        strip.setBackground(UITheme.BLACK);
        strip.setPreferredSize(new Dimension(10, 24));
        strip.add(UITheme.label("ADMIN: " + admin.getFullName().toUpperCase(),
                Font.PLAIN, 10f, 0.2f, Color.WHITE));
        header.add(strip, BorderLayout.NORTH);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(18, 60, 8, 60));
        bar.add(UITheme.label("DAMNIT  ·  ADMIN CONSOLE", Font.BOLD, 26f, 0.3f, UITheme.BLACK),
                BorderLayout.WEST);

        BlackButton logout = new BlackButton("LOG OUT");
        logout.setPreferredSize(new Dimension(140, 40));
        logout.setMaximumSize(new Dimension(140, 40));
        logout.addActionListener(e -> {
            dispose();
            new AuthFrame().setVisible(true);
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(logout);
        bar.add(right, BorderLayout.EAST);

        header.add(bar, BorderLayout.CENTER);
        return header;
    }

    // ---------- Products tab (CRUD) ----------

    private JComponent buildProductsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG);

        styleTable(productTable);
        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xCFCEC9)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setOpaque(false);
        actions.add(button("ADD PRODUCT", e -> addProduct()));
        actions.add(button("EDIT", e -> editProduct()));
        actions.add(button("DELETE", e -> deleteProduct()));
        actions.add(button("REFRESH", e -> refreshProducts()));
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshProducts() {
        productModel.setRowCount(0);
        for (Product p : DataStore.products) {
            productModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getCategory(), p.getSection(),
                    UITheme.money(p.getPrice()), p.getStock()});
        }
    }

    private void addProduct() {
        Product p = productDialog(null);
        if (p != null) {
            DataStore.products.add(p);
            refreshProducts();
        }
    }

    private void editProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a product first."); return; }
        if (productDialog(DataStore.products.get(row)) != null) refreshProducts();
    }

    private void deleteProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a product first."); return; }
        Product p = DataStore.products.get(row);
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete \"" + p.getName() + "\"?", "Delete product", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            DataStore.products.remove(row);
            refreshProducts();
        }
    }

    /*
     * Shared add/edit form. existing == null means add (and the ID is editable
     * and checked for uniqueness); otherwise it edits that product in place.
     * Returns the product on save, or null if cancelled or invalid.
     */
    private Product productDialog(Product existing) {
        boolean adding = existing == null;
        JTextField txtId = new JTextField(adding ? "" : existing.getId());
        JTextField txtName = new JTextField(adding ? "" : existing.getName());
        JTextField txtCategory = new JTextField(adding ? "" : existing.getCategory());
        JComboBox<String> cboSection = new JComboBox<>(SECTIONS);
        JTextField txtPrice = new JTextField(adding ? "" : String.valueOf(existing.getPrice()));
        JTextField txtStock = new JTextField(adding ? "" : String.valueOf(existing.getStock()));
        JTextField txtImage = new JTextField(adding ? "" : existing.getImageUrl());
        txtId.setEnabled(adding);
        if (!adding) cboSection.setSelectedItem(existing.getSection());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("ID"));          form.add(txtId);
        form.add(new JLabel("Name"));        form.add(txtName);
        form.add(new JLabel("Category"));    form.add(txtCategory);
        form.add(new JLabel("Section"));     form.add(cboSection);
        form.add(new JLabel("Price (PHP)")); form.add(txtPrice);
        form.add(new JLabel("Stock"));       form.add(txtStock);
        form.add(new JLabel("Image URL"));   form.add(txtImage);

        while (true) {
            int ok = JOptionPane.showConfirmDialog(this, form,
                    adding ? "Add product" : "Edit product",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return null;
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                String category = txtCategory.getText().trim();
                if (id.isEmpty() || name.isEmpty() || category.isEmpty()) {
                    throw new IllegalArgumentException("ID, name and category are required.");
                }
                double price = Double.parseDouble(txtPrice.getText().trim());
                int stock = Integer.parseInt(txtStock.getText().trim());
                if (price < 0 || stock < 0) {
                    throw new IllegalArgumentException("Price and stock cannot be negative.");
                }
                if (adding && findProduct(id) != null) {
                    throw new IllegalArgumentException("A product with ID " + id + " already exists.");
                }
                String section = (String) cboSection.getSelectedItem();
                String image = txtImage.getText().trim();
                if (adding) {
                    return new Product(id, name, category, section, price, stock, image);
                }
                existing.setName(name);
                existing.setCategory(category);
                existing.setSection(section);
                existing.setPrice(price);
                existing.setStock(stock);
                existing.setImageUrl(image);
                return existing;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Price and stock must be numbers.",
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Invalid input", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private Product findProduct(String id) {
        for (Product p : DataStore.products) {
            if (p.getId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    // ---------- Daily sales tab ----------

    private JComponent buildSalesTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG);

        styleTable(salesTable);
        JScrollPane scroll = new JScrollPane(salesTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xCFCEC9)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(lblSummary, BorderLayout.WEST);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(button("REFRESH", e -> refreshSales()));
        bottom.add(right, BorderLayout.EAST);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshSales() {
        salesModel.setRowCount(0);
        LocalDate today = LocalDate.now();
        int txns = 0, units = 0;
        double gross = 0;
        for (Sale s : DataStore.sales) {
            if (!s.getDateTime().toLocalDate().equals(today)) continue;
            salesModel.addRow(new Object[]{
                    s.getReceiptNo(), s.getDateTime().format(TIME), s.getCashierName(),
                    s.getItemCount(), UITheme.money(s.getTotal())});
            txns++;
            units += s.getItemCount();
            gross += s.getTotal();
        }
        lblSummary.setText(String.format("TODAY  ·  %d SALE%s  ·  %d UNITS  ·  GROSS %s",
                txns, txns == 1 ? "" : "S", units, UITheme.money(gross)));
    }

    // ---------- small helpers ----------

    private static void styleTable(JTable t) {
        t.setRowHeight(28);
        t.setFont(UITheme.font(Font.PLAIN, 12f, 0.02f));
        t.setGridColor(new Color(0xE0DFDC));
        t.setSelectionBackground(new Color(0xD6D5D1));
        t.setShowVerticalLines(false);
        t.getTableHeader().setFont(UITheme.font(Font.BOLD, 11f, 0.1f));
        t.getTableHeader().setReorderingAllowed(false);
    }

    private JButton button(String text, java.awt.event.ActionListener onClick) {
        JButton b = new JButton(text);
        b.setFont(UITheme.font(Font.BOLD, 11f, 0.12f));
        b.setForeground(UITheme.BLACK);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(UITheme.BLACK, 1));
        b.setPreferredSize(new Dimension(150, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(onClick);
        return b;
    }
}
