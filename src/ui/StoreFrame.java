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
                new String[]{"SEARCH", "CART"},
                this::onNav,
                this::goHome);
        buildStrip();
        root.add(header, BorderLayout.NORTH);

        center.setOpaque(false);
        center.add(new HeroPanel(() -> showShop(null, true)), HOME);
        center.add(buildShop(), SHOP);
        orderEntry = new OrderEntryPanel(order,
                this::updateCartLabel,
                () -> showShop(section, false),
                this::goToPayment);
        center.add(orderEntry, ORDER);
        paymentPanel = new PaymentPanel(order, cashier,
                this::showOrder,        // BACK TO ORDER
                this::onPaid);          // after a successful sale
        center.add(paymentPanel, PAYMENT);
        receiptPanel = new ReceiptPanel(() -> showShop(null, true));   // NEW ORDER
        center.add(receiptPanel, RECEIPT);
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

    /* Cashier name and LOG OUT inside the black strip. */
    private void buildStrip() {
        JPanel strip = header.getStrip();
        strip.add(UITheme.label("CASHIER: " + cashier.getFullName().toUpperCase(),
                Font.PLAIN, 10f, 0.2f, Color.WHITE));
        strip.add(UITheme.link("LOG OUT", this::logout));
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
                showShop(section, false);
                txtSearch.requestFocusInWindow();
                break;
            case "CART":
                showOrder();
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
            updateCartLabel();
        } catch (OutOfStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Out of stock", JOptionPane.WARNING_MESSAGE);
        } catch (InvalidQuantityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid quantity", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateCartLabel() {
        int qty = 0;
        for (OrderItem item : order.getItems()) qty += item.getQuantity();
        header.setLabelText("CART", qty == 0 ? "CART" : "CART (" + qty + ")");
    }

    private void showOrder() {
        header.setActive("CART");
        orderEntry.refresh();
        cards.show(center, ORDER);
    }

    // ---------- Payment ----------

    /* PROCEED TO PAYMENT on the order screen lands here. */
    private void goToPayment() {
        header.setActive("CART");
        paymentPanel.refresh();
        cards.show(center, PAYMENT);
    }

    /* Called by PaymentPanel after DataStore.completeSale() succeeded. */
    private void onPaid(Sale sale) {
        order.clear();              // otherwise the same items could be paid twice
        orderEntry.refresh();       // also updates the CART label
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
}