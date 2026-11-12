package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class HeroPanel extends JPanel {
	private BufferedImage model;
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	 public HeroPanel(Runnable onShop) {
	        setOpaque(false);
	        setLayout(new BorderLayout());
	 
	        try {
	            File f = new File("images/model.png");
	            if (f.exists()) model = ImageIO.read(f);
	        } catch (Exception ex) {
	            model = null;
	        }
	 
	        BlackButton btnShop = new BlackButton("SHOP NOW");
	        btnShop.setFont(UITheme.font(Font.BOLD, 16f, 0.05f));
	        btnShop.addActionListener(e -> onShop.run());
	 
	        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
	        south.setOpaque(false);
	        south.setBorder(BorderFactory.createEmptyBorder(0, 60, 50, 0));
	        south.add(btnShop);
	        add(south, BorderLayout.SOUTH);
	    }
	 
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D) g.create();
	        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	 
	        int w = getWidth();
	        int h = getHeight();
	 
	        // Big text scales with the window width
	        Font f = UITheme.font(Font.BOLD, Math.max(60f, w * 0.115f), 0.35f);
	        g2.setFont(f);
	        g2.setColor(UITheme.BLACK);
	        int baseline = (int) (h * 0.5);
	        int itWidth = (int) f.getStringBounds("IT", g2.getFontRenderContext()).getWidth();
	        g2.drawString("DAMN", 60, baseline);
	        g2.drawString("IT", w - 60 - itWidth, baseline);
	 
	        // Model stands between DAMN and IT, slightly cropped at the bottom
	        if (model != null) {
	            int mh = (int) (h * 0.95);
	            int mw = (int) (model.getWidth() * ((double) mh / model.getHeight()));
	            g2.drawImage(model, (int) (w * 0.62) - mw / 2, (int) (h * 0.1), mw, mh, null);
	        }
	        g2.dispose();
	    }
}
