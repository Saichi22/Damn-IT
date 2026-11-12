package ui;

import javax.swing.JPanel;
import java.awt.*;
import javax.swing.*;

/*
 * Base layout shared by Login, Sign Up and Forgot Password:
 * a big tracked title, then fields stacked on the left side.
 */
public abstract class FormPanel extends JPanel {
	protected static final int FIELD_WIDTH = 420;
    private final JPanel box = new JPanel();
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	 protected FormPanel(String title) {
	        setOpaque(false);
	        setLayout(new GridBagLayout());
	 
	        box.setOpaque(false);
	        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
	        box.setBorder(BorderFactory.createEmptyBorder(0, 70, 0, 0));
	 
	        JLabel heading = UITheme.label(title, Font.BOLD, 32f, 0.15f, UITheme.BLACK);
	        heading.setAlignmentX(LEFT_ALIGNMENT);
	        box.add(heading);
	        box.add(Box.createVerticalStrut(28));
	 
	        GridBagConstraints gc = new GridBagConstraints();
	        gc.anchor = GridBagConstraints.WEST;
	        gc.weightx = 1;
	        gc.weighty = 1;
	        add(box, gc);
	    }
	 
	    /* Adds a small caption above a field. */
	    protected void addField(String caption, JComponent field) {
	        JLabel l = UITheme.label(caption.toUpperCase(), Font.PLAIN, 10f, 0.2f, UITheme.GRAY);
	        l.setAlignmentX(LEFT_ALIGNMENT);
	        field.setAlignmentX(LEFT_ALIGNMENT);
	        field.setPreferredSize(new Dimension(FIELD_WIDTH, 36));
	        field.setMaximumSize(new Dimension(FIELD_WIDTH, 36));
	        box.add(l);
	        box.add(Box.createVerticalStrut(2));
	        box.add(field);
	        box.add(Box.createVerticalStrut(12));
	    }
	 
	    protected void addComponent(JComponent c) {
	        c.setAlignmentX(LEFT_ALIGNMENT);
	        box.add(c);
	    }
	 
	    protected void addGap(int height) {
	        box.add(Box.createVerticalStrut(height));
	    }
	 
	    protected void showError(String message) {
	        JOptionPane.showMessageDialog(this, message, "Please check your input", JOptionPane.ERROR_MESSAGE);
	    }
	 
	    /* Called each time the screen is shown, to wipe old input. */
	    public abstract void clearFields();
}
