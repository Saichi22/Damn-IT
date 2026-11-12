package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/* Solid black rectangular button, like "SHOP NOW" in the mockup. */
public class BlackButton extends JButton {
    private boolean hover = false;

    public BlackButton(String text) {
        super(text);
        setFont(UITheme.font(Font.BOLD, 13f, 0.12f));
        setForeground(Color.WHITE);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(420, 48));
        setMaximumSize(new Dimension(420, 48));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(hover ? UITheme.HOVER : UITheme.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);   // draws the text on top
    }
}