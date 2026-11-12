package ui;

import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Sale;

/*
 * Receipt screen shown right after a successful payment.
 *   - shows the receipt as a narrow paper strip (monospaced text)
 *   - PRINT sends the same text to a printer
 *   - SAVE opens a save dialog that already has the file name filled in (<receipt no>.txt)
 *   - NEW ORDER goes back to the shop
 * The text comes only from the Sale object, so it never changes after the sale,
 * even if the product list changes later.
 */
public class ReceiptPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 40;                        // characters per line
    private static final File SAVE_DIR = new File("receipts");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Runnable onNewOrder;
    private final JTextArea paper = new JTextArea();
    private Sale current;

    public ReceiptPanel(Runnable onNewOrder) {
        this.onNewOrder = onNewOrder;

        setOpaque(false);
        setLayout(new BorderLayout());
        add(buildTopBar(), BorderLayout.NORTH);
        add(buildPaper(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
    }

    // ---------- Layout ----------

    private JPanel buildTopBar() {
        JPanel titleBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        titleBox.setOpaque(false);
        titleBox.add(UITheme.label("RECEIPT", Font.BOLD, 28f, 0.2f, UITheme.BLACK));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 60, 16, 60));
        bar.add(titleBox, BorderLayout.WEST);
        return bar;
    }

    private JComponent buildPaper() {
        paper.setEditable(false);
        paper.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        paper.setForeground(UITheme.BLACK);
        paper.setBackground(Color.WHITE);
        paper.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        paper.setFocusable(false);

        JScrollPane scroll = new JScrollPane(paper);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xCFCECA)));
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        scroll.setPreferredSize(new Dimension(440, 10));

        // keeps the strip centered and as tall as the screen allows
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);
        holder.setBorder(BorderFactory.createEmptyBorder(0, 60, 16, 60));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.VERTICAL;
        gc.weighty = 1;
        holder.add(scroll, gc);
        return holder;
    }

    private JPanel buildButtons() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(0, 60, 36, 60));
        row.add(button("PRINT", this::print));
        row.add(button("SAVE AS TEXT", this::save));
        row.add(button("NEW ORDER", onNewOrder));
        return row;
    }

    private BlackButton button(String text, Runnable action) {
        BlackButton b = new BlackButton(text);
        b.setPreferredSize(new Dimension(200, 46));
        b.setMaximumSize(new Dimension(200, 46));
        b.addActionListener(e -> action.run());
        return b;
    }

    // ---------- Showing a sale ----------

    /* Call each time a sale finishes. */
    public void show(Sale sale) {
        current = sale;
        paper.setText(format(sale));
        paper.setCaretPosition(0);
    }

    // ---------- Actions ----------

    private void print() {
        if (current == null) return;
        try {
            // a fresh text area with no screen padding, so the printout has normal margins
            JTextArea toPrint = new JTextArea(format(current));
            toPrint.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
            toPrint.print();            // shows the system print dialog; false = cancelled
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Could not print the receipt: " + ex.getMessage()
                    + "\nYou can still use SAVE AS TEXT.", "Print failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    /* Opens a save dialog with the file name already filled in, e.g. receipts/R0001.txt. */
    private void save() {
        if (current == null) return;
        try {
            if (!SAVE_DIR.exists() && !SAVE_DIR.mkdirs()) {
                throw new IOException("Cannot create folder " + SAVE_DIR.getAbsolutePath());
            }

            JFileChooser chooser = new JFileChooser(SAVE_DIR);
            chooser.setDialogTitle("Save receipt");
            chooser.setFileFilter(new FileNameExtensionFilter("Text file (*.txt)", "txt"));
            chooser.setSelectedFile(new File(SAVE_DIR, current.getReceiptNo() + ".txt"));   // default name
            if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getParentFile(), file.getName() + ".txt");
            }
            if (file.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(this,
                        file.getName() + " already exists. Replace it?",
                        "Confirm save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (overwrite != JOptionPane.YES_OPTION) return;
            }

            Files.write(file.toPath(), format(current).getBytes(StandardCharsets.UTF_8));
            JOptionPane.showMessageDialog(this, "Saved to " + file.getAbsolutePath(),
                    "Receipt saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not save the receipt: " + ex.getMessage(),
                    "Save failed", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            // e.g. a file name with characters Windows does not allow (? * : etc.)
            JOptionPane.showMessageDialog(this, "Could not save the receipt. Check the file name and folder.",
                    "Save failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ---------- Receipt text ----------

    /*
     * Builds the whole receipt as plain text, WIDTH characters per line.
     * Amounts are plain numbers (1,990.00) so columns line up in any monospaced font;
     * the currency is stated once under the header.
     */
    public static String format(Sale s) {
        StringBuilder sb = new StringBuilder();
        sb.append(center("DAMNIT")).append('\n');
        sb.append(center("SALES RECEIPT")).append('\n');
        sb.append(center("(all amounts in PHP)")).append('\n');
        sb.append(rule('=')).append('\n');
        sb.append(row("RECEIPT NO:", s.getReceiptNo())).append('\n');
        sb.append(row("DATE:", s.getDateTime().format(DATE_FORMAT))).append('\n');
        sb.append(row("CASHIER:", s.getCashierName())).append('\n');
        sb.append(rule('=')).append('\n');
        sb.append(row("ITEM", "AMOUNT")).append('\n');
        sb.append(rule('-')).append('\n');

        for (Sale.Line line : s.getLines()) {
            for (String part : wrap(line.getName())) {
                sb.append(part).append('\n');
            }
            sb.append(row("  " + line.getQuantity() + " x " + amount(line.getUnitPrice()),
                    amount(line.getLineTotal()))).append('\n');
        }

        sb.append(rule('-')).append('\n');
        sb.append(row("TOTAL ITEMS", String.valueOf(s.getItemCount()))).append('\n');
        sb.append(row("SUBTOTAL", amount(s.getSubtotal()))).append('\n');
        if (s.getDiscount() > 0) {
            sb.append(row("DISCOUNT (10%)", "-" + amount(s.getDiscount()))).append('\n');
        }
        sb.append(row("VAT (12%)", amount(s.getTax()))).append('\n');
        sb.append(rule('=')).append('\n');
        sb.append(row("TOTAL", amount(s.getTotal()))).append('\n');
        sb.append(rule('=')).append('\n');
        sb.append(row("CASH", amount(s.getTendered()))).append('\n');
        sb.append(row("CHANGE", amount(s.getChange()))).append('\n');
        sb.append(rule('-')).append('\n');
        sb.append(center("THANK YOU FOR SHOPPING!")).append('\n');
        sb.append(center("PLEASE COME AGAIN")).append('\n');
        return sb.toString();
    }

    /* 1990 -> "1,990.00" (no currency symbol, so it never breaks the alignment). */
    private static String amount(double value) {
        return String.format(java.util.Locale.US, "%,.2f", value);   // same look on any computer
    }

    private static String rule(char c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < WIDTH; i++) sb.append(c);
        return sb.toString();
    }

    private static String center(String text) {
        if (text.length() >= WIDTH) return text.substring(0, WIDTH);
        int pad = (WIDTH - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pad; i++) sb.append(' ');
        return sb.append(text).toString();
    }

    /* Left text and right text on one line; the left side is cut if the line would overflow. */
    private static String row(String left, String right) {
        int room = WIDTH - right.length() - 1;
        if (left.length() > room) left = left.substring(0, Math.max(0, room));
        StringBuilder sb = new StringBuilder(left);
        for (int i = left.length() + right.length(); i < WIDTH; i++) sb.append(' ');
        return sb.append(right).toString();
    }

    /* Splits a long product name over several lines. */
    private static java.util.List<String> wrap(String text) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        String rest = text == null ? "" : text.trim();
        while (rest.length() > WIDTH) {
            int cut = rest.lastIndexOf(' ', WIDTH);
            if (cut <= 0) cut = WIDTH;          // one very long word: hard cut
            lines.add(rest.substring(0, cut).trim());
            rest = rest.substring(cut).trim();
        }
        lines.add(rest);
        return lines;
    }
}