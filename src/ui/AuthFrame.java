package ui;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class AuthFrame extends JFrame {
	public static final String LOGIN = "login";
	public static final String SIGNUP = "signup";
	public static final String FORGOT = "forgot";
	private final CardLayout cards = new CardLayout();
	private final JPanel cardPanel = new JPanel(cards);
	private final Map<String, FormPanel> screens = new HashMap<>();
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					AuthFrame frame = new AuthFrame();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public AuthFrame() {
        setTitle("DAMNIT");
        setSize(1160, 760);
        setMinimumSize(new Dimension(1100, 740));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
 
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG);
        setContentPane(root);
 
        root.add(buildHeader(), BorderLayout.NORTH);
 
        // Forms on the left, illustration on the right
        cardPanel.setOpaque(false);
        addScreen(LOGIN, new LoginPanel(this));
        addScreen(SIGNUP, new SignUpPanel(this));
        addScreen(FORGOT, new ForgotPasswordPanel(this));
 
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(cardPanel, BorderLayout.CENTER);
        body.add(new ImagePanel("images/model.png"), BorderLayout.EAST);
        root.add(body, BorderLayout.CENTER);
 
        showCard(LOGIN);
    }
 
    private void addScreen(String name, FormPanel panel) {
        screens.put(name, panel);
        cardPanel.add(panel, name);
    }
 
    /* Switches screen and clears whatever was typed before. */
    public void showCard(String name) {
        screens.get(name).clearFields();
        cards.show(cardPanel, name);
    }
 
    // ---------- Header: black strip + nav bar ----------
 
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
 
        JPanel strip = new JPanel();
        strip.setBackground(UITheme.BLACK);
        strip.setPreferredSize(new Dimension(10, 20));
        header.add(strip, BorderLayout.NORTH);
 
        JPanel nav = new JPanel(new GridLayout(1, 3));
        nav.setOpaque(false);
        nav.setBorder(BorderFactory.createEmptyBorder(18, 60, 18, 60));
 
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 36, 0));
        left.setOpaque(false);
        for (String s : new String[]{"MEN", "WOMEN", "KIDS", "IT"}) {
            left.add(navLabel(s));
        }
 
        JLabel logo = UITheme.label("DAMNIT", Font.BOLD, 30f, 0.3f, UITheme.BLACK);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logo.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showCard(LOGIN); }
        });
 
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 28, 0));
        right.setOpaque(false);
        for (String s : new String[]{"SEARCH", "WISHLIST", "ORDER"}) {
            right.add(navLabel(s));
        }
 
        nav.add(left);
        nav.add(logo);
        nav.add(right);
        header.add(nav, BorderLayout.CENTER);
        return header;
    }
 
    private JLabel navLabel(String text) {
        return UITheme.label(text, Font.PLAIN, 12f, 0.25f, UITheme.BLACK);
    }

}