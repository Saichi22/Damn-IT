package ui;

import java.awt.*;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import data.DataStore;
import exception.InsufficientPaymentException;
import exception.OutOfStockException;
import model.Account;
import model.Order;
import model.OrderItem;
import model.Sale;

/*
 * Payment screen (cash only):
 *   - left: the items being paid for
 *   - right: bill summary, "amount received" field, live change, COMPLETE PAYMENT
 *
 * Input rules:
 *   - the field only accepts digits and one decimal point (max 2 decimals), so letters never get in
 *   - the CHANGE line updates while typing and turns red with "SHORT" when the cash is not enough
 *   - COMPLETE PAYMENT checks the text again and lets DataStore.completeSale() throw
 *     InsufficientPaymentException / OutOfStockException, which are shown as warnings
 *   - once the sale is recorded, nothing after it can bring back the "try again" message,
 *     so the same order can never be charged twice
 */
public class PaymentPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Color LINE = new Color(0xCFCECA);
    private static final Color ERROR_RED = new Color(0xB00020);
    private static final Color OK_GREEN = new Color(0x1B6E3A);
    private static final int RIGHT_WIDTH = 380;

    private final Order order;
    private final Account cashier;
    private final Runnable onBack;
    private final Consumer<Sale> onPaid;

    private final JPanel itemsBox = new JPanel();
    private final JLabel lblCount = UITheme.label("", Font.PLAIN, 11f, 0.2f, UITheme.GRAY);
    private final JLabel lblSubtotal = valueLabel(13f, Font.PLAIN);
    private final JLabel lblDiscount = valueLabel(13f, Font.PLAIN);
    private final JLabel lblTax = valueLabel(13f, Font.PLAIN);
    private final JLabel lblTotal = valueLabel(22f, Font.BOLD);
    private final JLabel lblChange = valueLabel(22f, Font.BOLD);
    private final JLabel lblError = UITheme.label(" ", Font.BOLD, 10f, 0.15f, ERROR_RED);
    private final JTextField txtTendered = new JTextField();
    private BlackButton btnPay;
    private boolean paying = false;     // blocks a second Enter / click while a payment is running

    public PaymentPanel(Order order, Account cashier, Runnable onBack, Consumer<Sale> onPaid) {
        this.order = order;
        this.cashier = cashier;
        this.onBack = onBack;
        this.onPaid = onPaid;

        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildItemsPart(), BorderLayout.CENTER);
        add(buildPaymentPart(), BorderLayout.EAST);
    }

    // ---------- Layout ----------

    private JPanel buildTopBar() {
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        titleBox.setOpaque(false);
        titleBox.add(UITheme.label("PAYMENT", Font.BOLD, 28f, 0.2f, UITheme.BLACK));
        titleBox.add(lblCount);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 60, 16, 60));
        bar.add(titleBox, BorderLayout.WEST);
        return bar;
    }

    private JPanel buildItemsPart() {
        itemsBox.setOpaque(false);
        itemsBox.setLayout(new BoxLayout(itemsBox, BoxLayout.Y_AXIS));

        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(itemsBox, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(24);

        JLabel heading = UITheme.label("ITEMS", Font.BOLD, 10f, 0.2f, UITheme.GRAY);
        heading.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BLACK),
                BorderFactory.createEmptyBorder(0, 8, 8, 8)));

        JPanel part = new JPanel(new BorderLayout(0, 0));
        part.setOpaque(false);
        part.setBorder(BorderFactory.createEmptyBorder(0, 60, 40, 0));
        part.add(heading, BorderLayout.NORTH);
        part.add(scroll, BorderLayout.CENTER);
        return part;
    }

    private JPanel buildPaymentPart() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

        JLabel heading = UITheme.label("BILL", Font.BOLD, 12f, 0.25f, UITheme.BLACK);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        box.add(heading);
        box.add(Box.createVerticalStrut(16));

        box.add(summaryRow("SUBTOTAL", lblSubtotal));
        box.add(Box.createVerticalStrut(8));
        box.add(summaryRow("DISCOUNT", lblDiscount));
        box.add(Box.createVerticalStrut(8));
        box.add(summaryRow("VAT (12%)", lblTax));
        box.add(Box.createVerticalStrut(12));

        JPanel rule = new JPanel();
        rule.setBackground(UITheme.BLACK);
        rule.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        rule.setPreferredSize(new Dimension(10, 1));
        rule.setAlignmentX(LEFT_ALIGNMENT);
        box.add(rule);
        box.add(Box.createVerticalStrut(12));

        box.add(summaryRow("TOTAL DUE", lblTotal));
        box.add(Box.createVerticalStrut(26));

        // amount received
        JLabel caption = UITheme.label("AMOUNT RECEIVED (CASH)", Font.PLAIN, 10f, 0.2f, UITheme.GRAY);
        caption.setAlignmentX(LEFT_ALIGNMENT);
        box.add(caption);
        box.add(Box.createVerticalStrut(2));

        UITheme.styleField(txtTendered);
        txtTendered.setFont(UITheme.font(Font.BOLD, 20f, 0.05f));
        txtTendered.setPreferredSize(new Dimension(RIGHT_WIDTH, 44));
        txtTendered.setMaximumSize(new Dimension(RIGHT_WIDTH, 44));
        txtTendered.setAlignmentX(LEFT_ALIGNMENT);
        ((AbstractDocument) txtTendered.getDocument()).setDocumentFilter(new AmountFilter());
        txtTendered.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateChange(); }
            @Override public void removeUpdate(DocumentEvent e) { updateChange(); }
            @Override public void changedUpdate(DocumentEvent e) { updateChange(); }
        });
        txtTendered.addActionListener(e -> pay());      // Enter completes the payment
        box.add(txtTendered);
        box.add(Box.createVerticalStrut(10));

        // quick amounts
        JPanel quick = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        quick.setOpaque(false);
        quick.setAlignmentX(LEFT_ALIGNMENT);
        quick.setMaximumSize(new Dimension(RIGHT_WIDTH, 38));
        // Locale.US: always a dot as the decimal mark, which is what the field accepts
        quick.add(quickButton("EXACT", () -> setTendered(String.format(Locale.US, "%.2f", order.getTotal()))));
        for (int amount : new int[]{100, 500, 1000}) {
            quick.add(quickButton("\u20B1" + String.format("%,d", amount), () -> setTendered(String.valueOf(amount))));
        }
        // FlowLayout adds its own left gap; pull the row back so it lines up with the field
        quick.setBorder(BorderFactory.createEmptyBorder(0, -8, 0, 0));
        box.add(quick);
        box.add(Box.createVerticalStrut(18));

        box.add(summaryRow("CHANGE", lblChange));
        box.add(Box.createVerticalStrut(6));

        lblError.setAlignmentX(LEFT_ALIGNMENT);
        box.add(lblError);
        box.add(Box.createVerticalStrut(14));

        btnPay = new BlackButton("COMPLETE PAYMENT");
        btnPay.setPreferredSize(new Dimension(RIGHT_WIDTH, 48));
        btnPay.setMaximumSize(new Dimension(RIGHT_WIDTH, 48));
        btnPay.setAlignmentX(LEFT_ALIGNMENT);
        btnPay.addActionListener(e -> pay());
        box.add(btnPay);
        box.add(Box.createVerticalStrut(16));

        JLabel back = UITheme.link("BACK TO ORDER", onBack);
        back.setAlignmentX(LEFT_ALIGNMENT);
        box.add(back);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(RIGHT_WIDTH + 100, 10));
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 60));
        wrap.add(box, BorderLayout.NORTH);
        return wrap;
    }

    private JPanel summaryRow(String caption, JLabel value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(UITheme.label(caption, Font.PLAIN, 11f, 0.2f, UITheme.GRAY), BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 32));
        return row;
    }

    private static JLabel valueLabel(float size, int style) {
        return UITheme.label("", style, size, 0.05f, UITheme.BLACK);
    }

    private BlackButton quickButton(String text, Runnable action) {
        BlackButton b = new BlackButton(text);
        b.setFont(UITheme.font(Font.BOLD, 11f, 0.1f));
        b.setPreferredSize(new Dimension(80, 34));
        b.setMaximumSize(new Dimension(80, 34));
        b.addActionListener(e -> {
            action.run();
            txtTendered.requestFocusInWindow();
        });
        return b;
    }

    // ---------- Showing the screen ----------

    /* Call every time this screen is shown: reloads the items and totals and clears old input. */
    public void refresh() {
        itemsBox.removeAll();
        int units = 0;
        for (OrderItem item : order.getItems()) {
            itemsBox.add(itemRow(item));
            units += item.getQuantity();
        }
        itemsBox.revalidate();
        itemsBox.repaint();
        lblCount.setText(units + (units == 1 ? " ITEM" : " ITEMS"));

        lblSubtotal.setText(UITheme.money(order.getSubtotal()));
        lblDiscount.setText(order.getDiscount() > 0 ? "-" + UITheme.money(order.getDiscount())
                                                    : UITheme.money(0));
        lblTax.setText(UITheme.money(order.getTax()));
        lblTotal.setText(UITheme.money(order.getTotal()));

        txtTendered.setText("");
        clearError();
        updateChange();
        paying = false;
        btnPay.setEnabled(!order.getItems().isEmpty());
        SwingUtilities.invokeLater(txtTendered::requestFocusInWindow);
    }

    private JPanel itemRow(OrderItem item) {
        JLabel name = UITheme.label(item.getQuantity() + " x " + item.getProduct().getName(),
                Font.PLAIN, 13f, 0.03f, UITheme.BLACK);
        name.setToolTipText(item.getProduct().getName());
        JLabel total = UITheme.label(UITheme.money(item.getLineTotal()), Font.BOLD, 13f, 0.03f, UITheme.BLACK);

        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                BorderFactory.createEmptyBorder(14, 8, 14, 8)));
        row.add(name, BorderLayout.CENTER);
        row.add(total, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Short.MAX_VALUE, 50));
        return row;
    }

    // ---------- Input handling ----------

    private void setTendered(String text) {
        txtTendered.setText(text);
    }

    /* Removes spaces and a trailing dot, so "500." counts as 500. */
    private static String normalize(String text) {
        String t = text == null ? "" : text.trim();
        if (t.endsWith(".")) t = t.substring(0, t.length() - 1);
        return t;
    }

    /* Checks only the text. Returns null when it is a usable amount, otherwise the message. */
    private static String checkAmountText(String text) {
        String t = normalize(text);
        if (t.isEmpty()) return "Enter the amount received.";
        if (!t.matches("\\d+(\\.\\d{1,2})?|\\.\\d{1,2}")) return "Amount must be a number, for example 500 or 500.50.";
        if (Double.parseDouble(t) <= 0) return "Amount must be greater than zero.";
        return null;
    }

    /* Live CHANGE / SHORT line under the field. */
    private void updateChange() {
        if (checkAmountText(txtTendered.getText()) != null) {
            lblChange.setText(UITheme.money(0));
            lblChange.setForeground(UITheme.GRAY);
            return;
        }
        long paid = Math.round(Double.parseDouble(normalize(txtTendered.getText())) * 100);
        long due = Math.round(order.getTotal() * 100);
        if (paid >= due) {
            lblChange.setText(UITheme.money((paid - due) / 100.0));
            lblChange.setForeground(OK_GREEN);
        } else {
            lblChange.setText("SHORT " + UITheme.money((due - paid) / 100.0));
            lblChange.setForeground(ERROR_RED);
        }
    }

    private void showError(String message) {
        lblError.setText(message.toUpperCase());
    }

    private void clearError() {
        lblError.setText(" ");
    }

    private void warn(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void pay() {
        if (paying) return;                       // ignore a second Enter/click
        if (order.getItems().isEmpty()) {         // Enter in the field still works when the button is grey
            showError("The order is empty. Go back and add items.");
            return;
        }
        String bad = checkAmountText(txtTendered.getText());
        if (bad != null) {
            showError(bad);
            warn(bad, "Invalid amount");
            txtTendered.requestFocusInWindow();
            return;
        }
        paying = true;
        btnPay.setEnabled(false);
        Sale sale = null;
        try {
            double tendered = Double.parseDouble(normalize(txtTendered.getText()));
            sale = DataStore.completeSale(order, tendered, cashier);
        } catch (InsufficientPaymentException ex) {
            showError(ex.getMessage());
            warn(ex.getMessage(), "Insufficient payment");
            txtTendered.requestFocusInWindow();
            txtTendered.selectAll();
        } catch (OutOfStockException ex) {
            warn(ex.getMessage(), "Out of stock");
            onBack.run();                       // let the cashier fix the quantity
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage(), "Cannot complete payment");
            onBack.run();
        } catch (RuntimeException ex) {         // anything unexpected: tell the cashier
            ex.printStackTrace();
            warn("Something went wrong. Please check the order and try again.", "Payment error");
        } finally {
            paying = false;
            btnPay.setEnabled(!order.getItems().isEmpty());
        }

        // Outside the try on purpose: the sale is already recorded and stock already lowered here.
        // If showing the receipt fails, the cashier must NOT be told to "try again" (double charge).
        if (sale != null) {
            clearError();
            try {
                onPaid.accept(sale);
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                warn("Payment was recorded as receipt " + sale.getReceiptNo()
                        + ", but the receipt screen could not be shown.\nDo not charge the customer again.",
                        "Receipt error");
            }
        }
    }

    /* Allows only digits and one decimal point with at most 2 decimals (and 7 whole digits). */
    private class AmountFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
            replace(fb, offset, 0, s, a);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet a)
                throws BadLocationException {
            if (s == null || s.isEmpty()) {
                super.replace(fb, offset, length, s, a);
                return;
            }
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String result = current.substring(0, offset) + s + current.substring(offset + length);
            if (!result.matches("\\d{0,7}(\\.\\d{0,2})?")) {
                Toolkit.getDefaultToolkit().beep();
                showError("Digits only, with at most 2 decimals, for example 500.50.");
                return;
            }
            clearError();
            super.replace(fb, offset, length, s, a);
        }
    }
}