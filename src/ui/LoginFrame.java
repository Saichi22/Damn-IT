package ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.*;
import java.awt.*;
import data.DataStore;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private JTextField txtUser = new JTextField(15);
	private JPasswordField txtPass = new JPasswordField(15);
	public LoginFrame() {
		setTitle("Brew & Bean POS - Login");
        setSize(380, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        panel.add(txtUser);
        panel.add(new JLabel("Password:"));
        panel.add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> doLogin());
        panel.add(new JLabel());
        panel.add(btnLogin);

        add(panel);
	}
	 private void doLogin() {
	        String user = txtUser.getText().trim();
	        String pass = new String(txtPass.getPassword());

	        if (user.isEmpty() || pass.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
	            return;
	        }
	        if (DataStore.login(user, pass)) {
	            JOptionPane.showMessageDialog(this, "Welcome, " + user + "!");
	            // TODO: open MainFrame here, then dispose() this window
	        } else {
	            JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    }
}
