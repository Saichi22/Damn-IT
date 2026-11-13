package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import exception.InvalidQuantityException;
import exception.OutOfStockException;
import model.Order;
import model.OrderItem;

/*
 * Order Entry screen: shows the current order in a table.
 *   - change quantity (the - / + buttons, or double-click the QTY cell and type)
 *   - remove an item (trash icon at the end of its row, or the DELETE key)
 *   - clear the whole order
 * A summary box on the right shows subtotal, discount, tax and total.
 * All changes go through the Order object, so stock checks and
 * exceptions are handled in one place.
 *
 * UX rules:
 *   - a row is always selected when the order has items, so - / + work right away
 *   - buttons that cannot act are disabled instead of showing "select an item" popups
 *   - warnings from typing in the QTY cell are shown AFTER the editor closes
 */
public class OrderEntryPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String[] COLUMNS = {"CODE", "ITEM", "PRICE", "QTY", "TOTAL", ""};
    private static final int QTY_COLUMN = 3;
    private static final int DELETE_COLUMN = 5;       // trash icon column
    private static final Color LINE = new Color(0xCFCECA);
    private static final Color ERROR_RED = new Color(0xB00020);
    private static final int STEP_ZONE = 32;          // width of the - and + click areas inside the QTY cell
    private static final int MAX_QTY_DIGITS = 4;      // longest quantity that can be typed

    private final Order order;
    private final Runnable onChange;        // lets StoreFrame update ORDER (3)
    private final Runnable onBackToShop;
    private final Runnable onCheckout;

    private final ItemTableModel model = new ItemTableModel();
    private final JTable table = new JTable(model);
    private final CardLayout tableCards = new CardLayout();
    private final JPanel tableHolder = new JPanel(tableCards);

    private final JLabel lblCount = UITheme.label("", Font.PLAIN, 11f, 0.2f, UITheme.GRAY);
    private final JLabel lblError = UITheme.label(" ", Font.BOLD, 10f, 0.15f, ERROR_RED);   // inline input message
    private final JLabel lblSubtotal = valueLabel(13f, Font.PLAIN);
    private final JLabel lblDiscount = valueLabel(13f, Font.PLAIN);
    private final JLabel lblTax = valueLabel(13f, Font.PLAIN);
    private final JLabel lblTotal = valueLabel(22f, Font.BOLD);
    private final JCheckBox chkDiscount = new JCheckBox("APPLY 10% DISCOUNT (SENIOR / PWD)");

    private BlackButton btnClear;
    private BlackButton btnPay;

    public OrderEntryPanel(Order order, Runnable onChange, Runnable onBackToShop, Runnable onCheckout) {
        this.order = order;
        this.onChange = onChange;
        this.onBackToShop = onBackToShop;
        this.onCheckout = onCheckout;

        setOpaque(false);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildTablePart(), BorderLayout.CENTER);
        add(buildSummaryPart(), BorderLayout.EAST);

        refresh();
    }

    // ---------- Layout ----------

    private JPanel buildTopBar() {
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        titleBox.setOpaque(false);
        titleBox.add(UITheme.label("CURRENT ORDER", Font.BOLD, 28f, 0.2f, UITheme.BLACK));
        titleBox.add(lblCount);

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 60, 16, 60));
        bar.add(titleBox, BorderLayout.WEST);
        return bar;
    }

    private JPanel buildTablePart() {
        styleTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UITheme.BG);
        scroll.setOpaque(false);

        // "empty order" card
        JPanel empty = new JPanel(new GridBagLayout());
        empty.setOpaque(false);
        JPanel emptyBox = new JPanel();
        emptyBox.setOpaque(false);
        emptyBox.setLayout(new BoxLayout(emptyBox, BoxLayout.Y_AXIS));
        JLabel msg = UITheme.label("THE ORDER IS EMPTY", Font.BOLD, 14f, 0.25f, UITheme.GRAY);
        JLabel browse = UITheme.link("BROWSE PRODUCTS", onBackToShop);
        msg.setAlignmentX(CENTER_ALIGNMENT);
        browse.setAlignmentX(CENTER_ALIGNMENT);
        emptyBox.add(msg);
        emptyBox.add(Box.createVerticalStrut(12));
        emptyBox.add(browse);
        empty.add(emptyBox);

        tableHolder.setOpaque(false);
        tableHolder.add(scroll, "table");
        tableHolder.add(empty, "empty");

        // button under the table (items are removed with the trash icon in each row)
        btnClear = smallButton("CLEAR ORDER", 140, this::clearOrder);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tools.setOpaque(false);
        tools.add(btnClear);
        tools.add(UITheme.label("TIP: USE - / + ON A ROW, OR DOUBLE-CLICK THE NUMBER TO TYPE IT", Font.PLAIN, 10f, 0.15f, UITheme.GRAY));

        JPanel part = new JPanel(new BorderLayout(0, 16));
        part.setOpaque(false);
        part.setBorder(BorderFactory.createEmptyBorder(0, 60, 40, 0));
        part.add(tableHolder, BorderLayout.CENTER);
        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.setOpaque(false);
        south.add(tools, BorderLayout.NORTH);
        south.add(lblError, BorderLayout.SOUTH);
        part.add(south, BorderLayout.SOUTH);
        return part;
    }

    private JPanel buildSummaryPart() {
        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

        JLabel heading = UITheme.label("ORDER SUMMARY", Font.BOLD, 12f, 0.25f, UITheme.BLACK);
        heading.setAlignmentX(LEFT_ALIGNMENT);
        box.add(heading);
        box.add(Box.createVerticalStrut(18));

        box.add(summaryRow("SUBTOTAL", lblSubtotal));
        box.add(Box.createVerticalStrut(8));
        box.add(summaryRow("DISCOUNT", lblDiscount));
        box.add(Box.createVerticalStrut(8));
        box.add(summaryRow("VAT (12%)", lblTax));
        box.add(Box.createVerticalStrut(14));

        JPanel rule = new JPanel();
        rule.setBackground(UITheme.BLACK);
        rule.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        rule.setPreferredSize(new Dimension(10, 1));
        rule.setAlignmentX(LEFT_ALIGNMENT);
        box.add(rule);
        box.add(Box.createVerticalStrut(14));

        box.add(summaryRow("TOTAL", lblTotal));
        box.add(Box.createVerticalStrut(22));

        chkDiscount.setOpaque(false);
        chkDiscount.setFocusPainted(false);
        chkDiscount.setFont(UITheme.font(Font.PLAIN, 10f, 0.12f));
        chkDiscount.setAlignmentX(LEFT_ALIGNMENT);
        chkDiscount.addActionListener(e -> {
            order.setDiscountApplied(chkDiscount.isSelected());
            updateTotals();
        });
        box.add(chkDiscount);
        box.add(Box.createVerticalStrut(24));

        btnPay = new BlackButton("PROCEED TO PAYMENT");
        btnPay.setPreferredSize(new Dimension(300, 48));
        btnPay.setMaximumSize(new Dimension(300, 48));
        btnPay.setAlignmentX(LEFT_ALIGNMENT);
        btnPay.addActionListener(e -> checkout());
        box.add(btnPay);
        box.add(Box.createVerticalStrut(16));

        JLabel back = UITheme.link("CONTINUE SHOPPING", onBackToShop);
        back.setAlignmentX(LEFT_ALIGNMENT);
        box.add(back);
        box.add(Box.createVerticalGlue());

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(420, 10));
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

    private BlackButton smallButton(String text, int width, Runnable action) {
        BlackButton b = new BlackButton(text);
        b.setFont(UITheme.font(Font.BOLD, 11f, 0.15f));
        b.setPreferredSize(new Dimension(width, 38));
        b.setMaximumSize(new Dimension(width, 38));
        b.addActionListener(e -> action.run());
        return b;
    }

    // ---------- Table styling ----------

    private void styleTable() {
        table.setRowHeight(46);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setBackground(UITheme.BG);
        table.setForeground(UITheme.BLACK);
        table.setFont(UITheme.font(Font.PLAIN, 13f, 0.03f));
        table.setSelectionBackground(new Color(0xD5D4D0));
        table.setSelectionForeground(UITheme.BLACK);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        // typing a digit on a selected row must not silently start editing QTY
        table.putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);

        // Header: small gray caps with a black line under it
        DefaultTableCellRenderer head = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, false, false, row, col);
                setFont(UITheme.font(Font.BOLD, 10f, 0.2f));
                setForeground(UITheme.GRAY);
                setBackground(UITheme.BG);
                setHorizontalAlignment(col == QTY_COLUMN ? SwingConstants.CENTER
                        : col >= 2 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BLACK);
                setBorder(BorderFactory.createCompoundBorder(line,
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return this;
            }
        };
        table.getTableHeader().setDefaultRenderer(head);

        // Cells: thin separator line, numbers aligned right, full name in a tooltip
        DefaultTableCellRenderer cell = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                           boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, false, row, col);
                setHorizontalAlignment(col >= 2 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                setFont(UITheme.font(col == 4 || col == 3 ? Font.BOLD : Font.PLAIN, 13f, 0.03f));
                Border line = BorderFactory.createMatteBorder(0, 0, 1, 0, LINE);
                setBorder(BorderFactory.createCompoundBorder(line,
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                if (!sel) setBackground(UITheme.BG);
                setToolTipText(col == 1 ? String.valueOf(v) : null);   // long names get cut off
                return this;
            }
        };
        table.setDefaultRenderer(String.class, cell);
        table.getColumnModel().getColumn(QTY_COLUMN).setCellRenderer(new QtyRenderer());
        table.getColumnModel().getColumn(DELETE_COLUMN).setCellRenderer(new TrashRenderer());

        int[] widths = {80, 280, 110, 140, 120, 56};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        // the trash column keeps a fixed width
        table.getColumnModel().getColumn(DELETE_COLUMN).setMinWidth(56);
        table.getColumnModel().getColumn(DELETE_COLUMN).setMaxWidth(56);

        // QTY editor: styled like the table, right aligned, selects the old number so typing replaces it
        final JTextField editorField = new JTextField();
        editorField.setHorizontalAlignment(SwingConstants.CENTER);
        editorField.setFont(UITheme.font(Font.BOLD, 13f, 0.03f));
        editorField.setForeground(UITheme.BLACK);
        final Border normalBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BLACK),
                BorderFactory.createEmptyBorder(0, 6, 0, 6));
        final Border errorBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERROR_RED, 2),
                BorderFactory.createEmptyBorder(0, 5, 0, 5));
        editorField.setBorder(normalBorder);
        // only digits can be typed or pasted, and not too many of them
        ((AbstractDocument) editorField.getDocument()).setDocumentFilter(new DigitsOnlyFilter());

        @SuppressWarnings("serial")
        DefaultCellEditor qtyEditor = new DefaultCellEditor(editorField) {
            @Override
            public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int row, int col) {
                Component c = super.getTableCellEditorComponent(t, v, sel, row, col);
                editorField.setBorder(normalBorder);
                clearError();
                SwingUtilities.invokeLater(editorField::selectAll);
                return c;
            }

            /* Double-clicking on - or + must not open the typing editor. */
            @Override
            public boolean isCellEditable(EventObject e) {
                if (e instanceof MouseEvent && onStepZone((MouseEvent) e) != 0) return false;
                return super.isCellEditable(e);
            }

            /* Refuses to close the editor while the text is not a valid quantity. */
            @Override
            public boolean stopCellEditing() {
                String problem = checkQuantityText(editorField.getText());
                if (problem != null) {
                    editorField.setBorder(errorBorder);
                    showInputError(problem);
                    return false;               // editor stays open so the person can fix it
                }
                editorField.setBorder(normalBorder);
                clearError();
                return super.stopCellEditing();
            }

            @Override
            public void cancelCellEditing() {
                editorField.setBorder(normalBorder);
                clearError();
                super.cancelCellEditing();
            }
        };
        qtyEditor.setClickCountToStart(2);
        table.getColumnModel().getColumn(QTY_COLUMN).setCellEditor(qtyEditor);

        // Buttons follow the selection
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtons();
        });

        // Clicking - or + inside a row changes that row's quantity;
        // clicking the trash icon removes that row
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int trashRow = trashRowAt(e);
                if (trashRow >= 0) {
                    table.setRowSelectionInterval(trashRow, trashRow);
                    removeSelected();
                    return;
                }
                int zone = onStepZone(e);
                if (zone == 0) return;
                int row = table.rowAtPoint(e.getPoint());
                table.setRowSelectionInterval(row, row);
                changeQuantity(zone);
            }
        });
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean hand = onStepZone(e) != 0 || trashRowAt(e) >= 0;
                table.setCursor(Cursor.getPredefinedCursor(hand ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        });

        // DELETE key removes the selected row
        table.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("DELETE"), "removeRow");
        table.getActionMap().put("removeRow", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { removeSelected(); }
        });
    }

    /* The row whose trash icon is under the mouse, or -1 if the mouse is anywhere else. */
    private int trashRowAt(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (row < 0 || col != DELETE_COLUMN || row >= order.getItems().size()) return -1;
        return row;
    }

    /* -1 if the mouse is on a row's "-", +1 if on its "+", 0 anywhere else. */
    private int onStepZone(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());
        if (row < 0 || col != QTY_COLUMN || row >= order.getItems().size()) return 0;
        Rectangle r = table.getCellRect(row, col, false);
        int x = e.getX() - r.x;
        if (x < STEP_ZONE) return -1;
        if (x > r.width - STEP_ZONE) return +1;
        return 0;
    }

    /* Draws  [-]  2  [+]  inside the QTY cell. Clicks are handled by the table's mouse listener. */
    private class QtyRenderer implements TableCellRenderer {
        private final JPanel panel = new JPanel(new BorderLayout(6, 0));
        private final JLabel minus = stepLabel("-");
        private final JLabel plus = stepLabel("+");
        private final JLabel qty = new JLabel("", SwingConstants.CENTER);

        QtyRenderer() {
            qty.setFont(UITheme.font(Font.BOLD, 13f, 0.03f));
            qty.setForeground(UITheme.BLACK);
            panel.setOpaque(true);
            panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, LINE),
                    BorderFactory.createEmptyBorder(8, 4, 8, 4)));
            panel.add(minus, BorderLayout.WEST);
            panel.add(qty, BorderLayout.CENTER);
            panel.add(plus, BorderLayout.EAST);
        }

        private JLabel stepLabel(String text) {
            JLabel l = new JLabel(text, SwingConstants.CENTER);
            l.setFont(UITheme.font(Font.BOLD, 14f, 0f));
            l.setForeground(UITheme.BLACK);
            l.setBorder(BorderFactory.createLineBorder(UITheme.BLACK));
            l.setPreferredSize(new Dimension(26, 26));
            return l;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean focus, int row, int col) {
            qty.setText(String.valueOf(v));
            panel.setBackground(sel ? t.getSelectionBackground() : UITheme.BG);
            return panel;
        }
    }

    /* Draws a small trash can in the last column. Clicks are handled by the table's mouse listener. */
    private static class TrashRenderer extends JPanel implements TableCellRenderer {
        private static final long serialVersionUID = 1L;

        TrashRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, LINE));
            setToolTipText("Remove this item");
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean focus, int row, int col) {
            setBackground(sel ? t.getSelectionBackground() : UITheme.BG);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(UITheme.BLACK);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int cx = getWidth() / 2;
            int cy = (getHeight() - 1) / 2;       // minus 1 for the bottom separator line
            int top = cy - 9;

            g2.drawLine(cx - 8, top + 3, cx + 8, top + 3);            // lid
            g2.drawRect(cx - 3, top, 6, 3);                           // handle
            g2.drawPolygon(new int[]{cx - 6, cx + 6, cx + 5, cx - 5}, // body
                           new int[]{top + 6, top + 6, top + 18, top + 18}, 4);
            g2.drawLine(cx - 2, top + 9, cx - 2, top + 15);           // ribs
            g2.drawLine(cx + 2, top + 9, cx + 2, top + 15);
            g2.dispose();
        }
    }

    // ---------- Actions ----------

    private void changeQuantity(int delta) {
        int row = table.getSelectedRow();
        if (row < 0) return;
        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) return;   // invalid text: stay in the editor
        if (row >= order.getItems().size()) return;

        int newQty = order.getItems().get(row).getQuantity() + delta;
        if (newQty < 1) {              // going below 1 means "remove it"
            removeSelected();
            return;
        }
        String problem = tryApplyQuantity(row, String.valueOf(newQty));
        if (problem != null) warn(problem, "Check quantity");
        refresh(row);
    }

    /*
     * Used by the buttons and by typing in the QTY cell.
     * Returns null when it worked, otherwise the message to show.
     * It never opens a dialog itself, so the caller decides when to warn.
     */
    private String tryApplyQuantity(int row, String text) {
        if (row < 0 || row >= order.getItems().size()) return null;   // stale row: ignore
        String bad = checkQuantityText(text);         // empty, letters, decimals, zero, too large
        if (bad != null) return bad;
        try {
            order.setQuantity(row, Integer.parseInt(text.trim()));
            return null;
        } catch (OutOfStockException ex) {
            return ex.getMessage();
        } catch (InvalidQuantityException ex) {
            return ex.getMessage();
        } catch (IllegalArgumentException ex) {
            return ex.getMessage();
        }
    }

    /* Checks only the text. Returns null if it is a usable quantity, otherwise the message. */
    private static String checkQuantityText(String text) {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) return "Enter a quantity, for example 2.";
        if (!t.matches("\\d+")) return "Quantity must be a whole number, for example 2.";
        try {
            if (Integer.parseInt(t) < 1) return "Quantity must be at least 1. Use the trash icon to remove it.";
        } catch (NumberFormatException ex) {
            return "That number is too large.";
        }
        return null;
    }

    private void showInputError(String message) {
        lblError.setText(message.toUpperCase());
    }

    private void clearError() {
        lblError.setText(" ");
    }

    /* Blocks letters, spaces, symbols and over-long numbers while typing or pasting. */
    private class DigitsOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
            replace(fb, offset, 0, s, a);
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet a)
                throws BadLocationException {
            if (s == null || s.isEmpty()) {            // deleting text is always fine
                super.replace(fb, offset, length, s, a);
                return;
            }
            if (!s.matches("\\d+")) {
                Toolkit.getDefaultToolkit().beep();
                showInputError("Digits only. Letters, spaces, symbols and decimals are not allowed.");
                return;
            }
            int newLength = fb.getDocument().getLength() - length + s.length();
            if (newLength > MAX_QTY_DIGITS) {
                Toolkit.getDefaultToolkit().beep();
                showInputError("Too many digits. Maximum is " + MAX_QTY_DIGITS + ".");
                return;
            }
            clearError();
            super.replace(fb, offset, length, s, a);
        }
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= order.getItems().size()) return;
        if (table.isEditing()) table.getCellEditor().cancelCellEditing();
        order.removeItem(row);
        refresh(row);                  // refresh keeps the selection in range
    }

    private void clearOrder() {
        if (order.getItems().isEmpty()) return;
        int choice = JOptionPane.showConfirmDialog(this, "Remove every item from the order?",
                "Clear order", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            order.releaseAll();          // gives the stock back
            refresh(-1);
        }
    }

    private void checkout() {
        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) return;   // fix the quantity first
        if (order.getItems().isEmpty()) {
            warn("Add at least one item before paying.", "Order is empty");
            return;
        }
        try {
            order.validateStock();     // stock may have changed since the item was added
        } catch (OutOfStockException ex) {
            warn(ex.getMessage(), "Out of stock");
            refresh();
            return;
        }
        onCheckout.run();
    }

    private void warn(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    // ---------- Refreshing ----------

    /* Call whenever this screen is shown. Keeps the current selection if there is one. */
    public void refresh() {
        refresh(table.getSelectedRow());
    }

    /* Redraws everything and selects this row (clamped). With items, a row is always selected. */
    private void refresh(int selectRow) {
        if (table.isEditing()) table.getCellEditor().cancelCellEditing();
        model.fireTableDataChanged();

        int rows = model.getRowCount();
        if (rows > 0) {
            int r = Math.max(0, Math.min(selectRow, rows - 1));
            table.setRowSelectionInterval(r, r);
            table.scrollRectToVisible(table.getCellRect(r, 0, true));
        }
        chkDiscount.setSelected(order.isDiscountApplied());
        clearError();
        updateTotals();
        onChange.run();
    }

    private void updateTotals() {
        boolean empty = order.getItems().isEmpty();
        tableCards.show(tableHolder, empty ? "empty" : "table");

        int units = 0;
        for (OrderItem item : order.getItems()) units += item.getQuantity();
        lblCount.setText(units + (units == 1 ? " ITEM" : " ITEMS"));

        lblSubtotal.setText(UITheme.money(order.getSubtotal()));
        lblDiscount.setText(order.getDiscount() > 0 ? "-" + UITheme.money(order.getDiscount())
                                                    : UITheme.money(0));
        lblTax.setText(UITheme.money(order.getTax()));
        lblTotal.setText(UITheme.money(order.getTotal()));
        chkDiscount.setEnabled(!empty);
        updateButtons();
    }

    /* Greys out buttons that cannot do anything right now. */
    private void updateButtons() {
        if (btnClear == null || btnPay == null) return;
        boolean empty = order.getItems().isEmpty();
        btnClear.setEnabled(!empty);
        btnPay.setEnabled(!empty);
    }

    // ---------- Table model (reads straight from the Order) ----------

    private class ItemTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;

        @Override public int getRowCount() { return order.getItems().size(); }
        @Override public int getColumnCount() { return COLUMNS.length; }
        @Override public String getColumnName(int c) { return COLUMNS[c]; }
        @Override public Class<?> getColumnClass(int c) { return String.class; }
        @Override public boolean isCellEditable(int r, int c) { return c == QTY_COLUMN; }

        @Override
        public Object getValueAt(int r, int c) {
            OrderItem item = order.getItems().get(r);
            switch (c) {
                case 0: return item.getProduct().getId();
                case 1: return item.getProduct().getName();
                case 2: return UITheme.money(item.getProduct().getPrice());
                case 3: return String.valueOf(item.getQuantity());
                case 4: return UITheme.money(item.getLineTotal());
                default: return "";          // trash column: the renderer draws the icon
            }
        }

        /*
         * Called when the person finishes typing in the QTY cell.
         * Apply it, refresh just that row, and show any warning AFTER the editor
         * has closed. Opening a dialog right here makes the table lose focus,
         * which can end the edit a second time and show the dialog twice.
         */
        @Override
        public void setValueAt(Object value, int r, int c) {
            if (c != QTY_COLUMN || r < 0 || r >= order.getItems().size()) return;
            String problem = tryApplyQuantity(r, String.valueOf(value));
            fireTableRowsUpdated(r, r);
            updateTotals();
            onChange.run();
            if (problem != null) {
                SwingUtilities.invokeLater(() -> warn(problem, "Check quantity"));
            }
        }
    }
}