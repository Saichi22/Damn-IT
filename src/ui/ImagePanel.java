package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
	private BufferedImage image;
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	 public ImagePanel(String path) {
	        setOpaque(false);
	        setPreferredSize(new Dimension(480, 100));
	        try {
	            File f = new File(path);
	            if (f.exists()) {
	                image = ImageIO.read(f);
	            }
	        } catch (Exception ex) {
	            image = null;   // no image, no problem
	        }
	    }
	 
	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        if (image == null) return;
	 
	        Graphics2D g2 = (Graphics2D) g.create();
	        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	 
	        // Scale to the panel height, keep the aspect ratio, center horizontally
	        double scale = (double) getHeight() / image.getHeight();
	        int w = (int) (image.getWidth() * scale);
	        int h = getHeight();
	        int x = (getWidth() - w) / 2;
	        g2.drawImage(image, x, 0, w, h, null);
	        g2.dispose();
	    }

}
