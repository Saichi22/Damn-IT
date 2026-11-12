package app;

import javax.swing.SwingUtilities;
import ui.AuthFrame;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new AuthFrame().setVisible(true));
    }
}
