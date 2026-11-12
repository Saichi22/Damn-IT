package ui;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;


/*
 * Central place for the DAMNIT look: colors, fonts and styling helpers.
 * Change values here and every screen updates.
 */
public final class UITheme {
 
    public static final Color BG = new Color(0xE4E3E0);     // light gray page background
    public static final Color BLACK = Color.BLACK;
    public static final Color GRAY = new Color(0x6B6B68);   // small captions
    public static final Color HOVER = new Color(0x333333);  // button hover
 
    private static final String FAMILY = pickFamily();
 
    private UITheme() { }
 
    /*
     * Uses Montserrat if it is installed on the computer (like the mockup),
     * otherwise falls back to a similar sans-serif font.
     */
    private static String pickFamily() {
        String[] preferred = {"Montserrat", "Segoe UI", "Helvetica Neue", "Arial"};
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : preferred) {
            if (available.contains(f)) return f;
        }
        return Font.SANS_SERIF;
    }
 
    /* Font with letter-spacing ("tracking"), which gives the wide-spaced look. */
    public static Font font(int style, float size, float tracking) {
        Map<TextAttribute, Object> attrs = new HashMap<>();
        attrs.put(TextAttribute.TRACKING, tracking);
        return new Font(FAMILY, style, 12).deriveFont(size).deriveFont(attrs);
    }
 
    public static JLabel label(String text, int style, float size, float tracking, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size, tracking));
        l.setForeground(color);
        return l;
    }
 
    /* Clickable text that underlines on hover. */
    public static JLabel link(String text, Runnable action) {
        JLabel l = label(text, Font.PLAIN, 11f, 0.15f, BLACK);
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e) { setUnderline(l, true); }
            @Override public void mouseExited(MouseEvent e) { setUnderline(l, false); }
        });
        return l;
    }
 
    private static void setUnderline(JLabel l, boolean on) {
        Map<TextAttribute, Object> attrs = new HashMap<>();
        attrs.put(TextAttribute.UNDERLINE, on ? TextAttribute.UNDERLINE_ON : -1);
        l.setFont(l.getFont().deriveFont(attrs));
    }
 
    private static Border underline() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BLACK),
                BorderFactory.createEmptyBorder(6, 2, 6, 2));
    }
    public static String money(double amount) {
        return String.format("₱%,.2f", amount);
    }
    /* Flat input with only a bottom line, matching the minimal mockup. */
    public static void styleField(JTextComponent field) {
        field.setOpaque(false);
        field.setFont(font(Font.PLAIN, 14f, 0.05f));
        field.setForeground(BLACK);
        field.setCaretColor(BLACK);
        field.setBorder(underline());
    }
 
    public static void styleCombo(JComboBox<String> combo) {
        combo.setFont(font(Font.PLAIN, 13f, 0.03f));
        combo.setBackground(BG);
        combo.setForeground(BLACK);
        combo.setBorder(underline());
        combo.setFocusable(false);
    }
}