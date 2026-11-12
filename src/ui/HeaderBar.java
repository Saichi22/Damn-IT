package ui;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.JPanel;

public class HeaderBar extends JPanel {
	private final JPanel strip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 3));
	private final Map<String, JLabel> labels = new HashMap<>();

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
    public HeaderBar(String[] leftItems, String[] rightItems,
            Consumer<String> onNav, Runnable onLogo) {
super(new BorderLayout());
setOpaque(false);

strip.setBackground(UITheme.BLACK);
strip.setPreferredSize(new Dimension(10, 24));
add(strip, BorderLayout.NORTH);

JPanel nav = new JPanel(new GridLayout(1, 3));
nav.setOpaque(false);
nav.setBorder(BorderFactory.createEmptyBorder(18, 60, 18, 60));

JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 36, 0));
left.setOpaque(false);
for (String key : leftItems) {
   left.add(navItem(key, onNav));
}

JLabel logo = UITheme.link("DAMNIT", onLogo);
logo.setFont(UITheme.font(Font.BOLD, 30f, 0.3f));
logo.setForeground(UITheme.BLACK);
logo.setHorizontalAlignment(SwingConstants.CENTER);

JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 28, 0));
right.setOpaque(false);
for (String key : rightItems) {
   right.add(navItem(key, onNav));
}

nav.add(left);
nav.add(logo);
nav.add(right);
add(nav, BorderLayout.CENTER);
}

private JLabel navItem(String key, Consumer<String> onNav) {
JLabel l = UITheme.link(key, () -> onNav.accept(key));
labels.put(key, l);
return l;
}

/* The black strip on top; add small white labels here. */
public JPanel getStrip() {
return strip;
}

/* Changes the text of a nav item, e.g. CART -> CART (3). */
public void setLabelText(String key, String text) {
JLabel l = labels.get(key);
if (l != null) l.setText(text);
}

/* Makes one nav item bold (the current section). Pass null for none. */
public void setActive(String activeKey) {
for (Map.Entry<String, JLabel> e : labels.entrySet()) {
   int style = e.getKey().equals(activeKey) ? Font.BOLD : Font.PLAIN;
   e.getValue().setFont(UITheme.font(style, 12f, 0.25f));
}
}

}
