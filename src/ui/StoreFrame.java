package ui;

import java.awt.*;
import java.util.TreeSet;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import data.DataStore;
import exception.InvalidQuantityException;
import exception.OutOfStockException;
import model.Account;
import model.Order;
import model.OrderItem;
import model.Product;
import model.Sale;


/*
 * The store / home page shown after login.
 *   HOME card    : hero from the mockup with SHOP NOW
 *   SHOP card    : product grid with section (MEN / WOMEN / KIDS / IT),
 *                  category filter and search
 *   ORDER card   : current order (quantities, discount, totals)
 *   PAYMENT card : cash payment, then back to the shop with an empty order
 * The current order is kept in one Order object; ADD TO ORDER puts items in it.
 */
public class StoreFrame extends JFrame {
	private static final long serialVersionUID = 1L;
    private static final String HOME = "home";
    private static final String SHOP = "shop";
    private static final String ORDER = "order";
    private static final String PAYMENT = "payment";
    private static final String RECEIPT = "receipt";
    private static final String PROFILE = "profile"; 
    private static final String ALL_CATEGORIES = "ALL CATEGORIES";
    private static final int COLUMNS = 4;
    private OrderEntryPanel orderEntry;
    private PaymentPanel paymentPanel;
    private ReceiptPanel receiptPanel;

    private final Account cashier;
    private final Order order = new Order();

    private final CardLayout cards = new CardLayout();
    private final JPanel center = new JPanel(cards);
    private final HeaderBar header;

    private final JPanel grid = new JPanel(new GridLayout(0, COLUMNS, 28, 36));
    private final JLabel lblTitle = UITheme.label("", Font.BOLD, 28f, 0.2f, UITheme.BLACK);
    private final JLabel lblCount = UITheme.label("", Font.PLAIN, 11f, 0.2f, UITheme.GRAY);
    private final JTextField txtSearch = new JTextField();
    private final JComboBox<String> cboCategory = new JComboBox<>();
    private JScrollPane scroll;

    private String section = null;          // null = all sections
    private boolean updatingCombo = false;  // stops the combo from refreshing while we refill it


    public StoreFrame(Account cashier) {

        this.cashier = cashier;

        setTitle("DAMNIT - Store");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);

        header = new HeaderBar(
                new String[]{"MEN", "WOMEN", "KIDS", "IT"},
                new String[]{"SEARCH", "ORDER", "PROFILE", "LOG OUT"},
                this::onNav,
                this::goHome);
        buildStrip();
        root.add(header, BorderLayout.NORTH);

        center.setOpaque(false);
        center.add(new HeroPanel(() -> showShop(null, true)), HOME);
        center.add(buildShop(), SHOP);
        orderEntry = new OrderEntryPanel(order,
                this::updateOrderLabel,
                () -> showShop(section, false),
                this::goToPayment);
        center.add(orderEntry, ORDER);
        paymentPanel = new PaymentPanel(order, cashier,
                this::showOrder,        // BACK TO ORDER
                this::onPaid);          // after a successful sale
        center.add(paymentPanel, PAYMENT);
        receiptPanel = new ReceiptPanel(() -> showShop(null, true));   // NEW ORDER
        center.add(receiptPanel, RECEIPT);
        center.add(buildProfile(), PROFILE); 
        root.add(center, BorderLayout.CENTER); 

        // Refresh the grid when the person types or picks a category
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshGrid(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshGrid(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshGrid(); }
        });
        cboCategory.addActionListener(e -> {
            if (!updatingCombo) refreshGrid();
        });

        goHome();
    }

    // ---------- Header ----------

    private void buildStrip() {
        JPanel strip = header.getStrip();
        strip.add(UITheme.label("CASHIER: " + cashier.getFullName().toUpperCase(),
                Font.PLAIN, 10f, 0.2f, Color.WHITE));
    }
    
    private void showProfile() {
        header.setActive("PROFILE");
        cards.show(center, PROFILE);
    }

    private void onNav(String key) {
        switch (key) {
            case "MEN":
            case "WOMEN":
            case "KIDS":
            case "IT":
                showShop(key, true);
                break;
            case "SEARCH":
                showShop(null, true);          // all products, empty search box
                txtSearch.requestFocusInWindow();
                break;
            case "ORDER":
                showOrder();
                break;
            case "PROFILE":
                showProfile();
                break;
            case "LOG OUT":
                logout();
                break;
            default:
                break;
        }
    }

    private void goHome() {
        header.setActive(null);
        cards.show(center, HOME);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                order.getItems().isEmpty() ? "Log out?" : "Log out? The current order will be lost.",
                "Log out", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            order.releaseAll();        // the unpaid order's items go back to stock
            dispose();
            new AuthFrame().setVisible(true);
        }
    }

    // ---------- Shop view ----------

    private JPanel buildShop() {

        JPanel shop = new JPanel(new BorderLayout());
        shop.setOpaque(false);

        // Toolbar: title + count on the left, search and category on the right
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblCount);

        UITheme.styleField(txtSearch);
        txtSearch.setPreferredSize(new Dimension(220, 34));
        UITheme.styleCombo(cboCategory);
        cboCategory.setPreferredSize(new Dimension(200, 34));

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        tools.setOpaque(false);
        tools.add(UITheme.label("SEARCH", Font.PLAIN, 10f, 0.2f, UITheme.GRAY));
        tools.add(txtSearch);
        tools.add(cboCategory);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 60, 16, 60));
        bar.add(titleBox, BorderLayout.WEST);
        bar.add(tools, BorderLayout.EAST);

        // Scrollable product grid
        grid.setOpaque(false);
        ScrollHolder holder = new ScrollHolder();
        holder.setLayout(new BorderLayout());
        holder.setOpaque(false);
        holder.setBorder(BorderFactory.createEmptyBorder(0, 60, 40, 60));
        holder.add(grid, BorderLayout.NORTH);

        scroll = new JScrollPane(holder, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG);
        scroll.getVerticalScrollBar().setUnitIncrement(24);

        shop.add(bar, BorderLayout.NORTH);
        shop.add(scroll, BorderLayout.CENTER);
        return shop;
    }

    /* Shows the product grid for one section (null = everything). */
    private void showShop(String newSection, boolean resetSearch) {
        section = newSection;
        header.setActive(newSection);
        lblTitle.setText(newSection == null ? "ALL PRODUCTS" : newSection);
        if (resetSearch) txtSearch.setText("");
        fillCategories();
        refreshGrid();
        cards.show(center, SHOP);
    }

    private void fillCategories() {
        TreeSet<String> categories = new TreeSet<>();
        for (Product p : DataStore.products) {
            if (section == null || p.getSection().equals(section)) {
                categories.add(p.getCategory());
            }
        }
        updatingCombo = true;
        cboCategory.removeAllItems();
        cboCategory.addItem(ALL_CATEGORIES);
        for (String c : categories) cboCategory.addItem(c);
        updatingCombo = false;
    }

    /* Rebuilds the cards using the current section, category and search text. */
    private void refreshGrid() {
        grid.removeAll();

        String query = txtSearch.getText().trim().toLowerCase();
        String category = (String) cboCategory.getSelectedItem();
        boolean allCategories = category == null || category.equals(ALL_CATEGORIES);

        int shown = 0;
        for (Product p : DataStore.products) {
            if (section != null && !p.getSection().equals(section)) continue;
            if (!allCategories && !p.getCategory().equals(category)) continue;
            if (!query.isEmpty()
                    && !p.getName().toLowerCase().contains(query)
                    && !p.getId().toLowerCase().contains(query)) continue;
            grid.add(new ProductCard(p, this::addToOrder));
            shown++;
        }

        if (shown == 0) {
            grid.add(UITheme.label("NO ITEMS FOUND", Font.PLAIN, 12f, 0.2f, UITheme.GRAY));
        }
        lblCount.setText(shown + (shown == 1 ? " ITEM" : " ITEMS"));

        grid.revalidate();
        grid.repaint();
        SwingUtilities.invokeLater(() -> scroll.getVerticalScrollBar().setValue(0));
    }

    // ---------- Order ----------

    private void addToOrder(Product p) {
        try {
            order.addItem(p, 1);
            updateOrderLabel();
        } catch (OutOfStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Out of stock", JOptionPane.WARNING_MESSAGE);
        } catch (InvalidQuantityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid quantity", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateOrderLabel() {
        int qty = 0;
        for (OrderItem item : order.getItems()) qty += item.getQuantity();
        header.setLabelText("ORDER", qty == 0 ? "ORDER" : "ORDER (" + qty + ")");
    }

    private void showOrder() {
        header.setActive("ORDER");
        orderEntry.refresh();
        cards.show(center, ORDER);
    }

    // ---------- Payment ----------

    /* PROCEED TO PAYMENT on the order screen lands here. */
    private void goToPayment() {
        header.setActive("ORDER");
        paymentPanel.refresh();
        cards.show(center, PAYMENT);
    }

    /* Called by PaymentPanel after DataStore.completeSale() succeeded. */
    private void onPaid(Sale sale) {
        order.clear();              // otherwise the same items could be paid twice
        orderEntry.refresh();       // also updates the ORDER label
        receiptPanel.show(sale);
        header.setActive(null);
        cards.show(center, RECEIPT);
        // NEW ORDER on the receipt screen calls showShop(), which rebuilds the grid with current stock
    }

    /* Panel that always matches the scroll pane's width, so the grid never scrolls sideways. */
    private static class ScrollHolder extends JPanel implements Scrollable {
        private static final long serialVersionUID = 1L;
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) { return 24; }
        @Override public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) { return r.height - 40; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }
    }
    
 // ---------- Profile view ----------

    private JPanel buildProfile() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(440, 540));
        card.setBorder(BorderFactory.createLineBorder(new Color(0xE0DFDC), 1));

        // 1. Top Avatar / Monogram Box (Matches ProductCard's IMAGE_BG style)
        String initials = getInitials(cashier.getFullName());
        JPanel avatarBox = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2.setColor(new Color(0x9E9D99));
                Font brandFont = UITheme.font(Font.BOLD, 11f, 0.35f);
                g2.setFont(brandFont);
                g2.drawString("DAMNIT STAFF", 24, 28);

                // Centered Cashier Monogram
                g2.setColor(UITheme.BLACK);
                Font monoFont = UITheme.font(Font.BOLD, 36f, 0.2f);
                g2.setFont(monoFont);
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(initials);
                int th = fm.getAscent();
                g2.drawString(initials, (getWidth() - tw) / 2, (getHeight() + th) / 2 - 4);
                g2.dispose();
            }
        };
        avatarBox.setBackground(new Color(0xF1F0ED));
        avatarBox.setPreferredSize(new Dimension(440, 150));
        card.add(avatarBox, BorderLayout.NORTH);

        // 2. Center Account Info Stack
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(28, 36, 24, 36));

        JLabel lblSection = UITheme.label("STAFF ACCOUNT", Font.PLAIN, 10f, 0.2f, UITheme.GRAY);
        JLabel lblName = UITheme.label(cashier.getFullName().toUpperCase(), Font.BOLD, 18f, 0.08f, UITheme.BLACK);
        lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        body.add(lblSection);
        body.add(Box.createVerticalStrut(4));
        body.add(lblName);
        body.add(Box.createVerticalStrut(22));

        body.add(profileRow("USERNAME", "@" + cashier.getUsername()));
        body.add(Box.createVerticalStrut(16));
        body.add(profileRow("SECURITY QUESTION", cashier.getSecurityQuestion()));
        body.add(Box.createVerticalStrut(16));
        body.add(profileRow("SHIFT STATUS", "ACTIVE TERMINAL"));

        card.add(body, BorderLayout.CENTER);

        // 3. Bottom Action Bar (Matching ProductCard's BlackButton style)
        JPanel actions = new JPanel(new GridLayout(1, 2, 12, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(0, 36, 32, 36));

        JButton btnBack = new JButton("BACK TO STORE");
        btnBack.setFont(UITheme.font(Font.BOLD, 11f, 0.15f));
        btnBack.setForeground(UITheme.BLACK);
        btnBack.setBackground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(UITheme.BLACK, 1));
        btnBack.setPreferredSize(new Dimension(10, 40));
        btnBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> goHome());

        BlackButton btnLogout = new BlackButton("LOG OUT");
        btnLogout.setFont(UITheme.font(Font.BOLD, 11f, 0.15f));
        btnLogout.setPreferredSize(new Dimension(10, 40));
        btnLogout.addActionListener(e -> logout());

        actions.add(btnBack);
        actions.add(btnLogout);
        card.add(actions, BorderLayout.SOUTH);

        wrapper.add(card);
        return wrapper;
    }

    private JPanel profileRow(String labelText, String valueText) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 46));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xECEBE8)),
                BorderFactory.createEmptyBorder(0, 0, 8, 0)));

        JLabel lblKey = UITheme.label(labelText, Font.PLAIN, 10f, 0.2f, UITheme.GRAY);
        JLabel lblVal = UITheme.label(valueText != null ? valueText : "N/A", Font.PLAIN, 13f, 0.05f, UITheme.BLACK);

        row.add(lblKey, BorderLayout.NORTH);
        row.add(lblVal, BorderLayout.CENTER);
        return row;
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) return "ST";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
    }
    
}