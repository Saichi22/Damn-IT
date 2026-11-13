package ui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;
import javax.swing.*;
import model.Product;
import javax.swing.JPanel;

public class ProductCard extends JPanel {
	private static final Color IMAGE_BG = new Color(0xF1F0ED);
    private static final Color LOW_STOCK = new Color(0xB00020);
    private BufferedImage image;
    private final JPanel imageBox;
    private final Product product;
    private final JLabel stock;
    private final CardLayout actionCards = new CardLayout();
    private final JPanel actionWrap = new JPanel(actionCards);
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ProductCard(Product product, Consumer<Product> onAdd) {
        this.product = product;
        setOpaque(false);
        setLayout(new BorderLayout());
 
        // ----- picture (drawn to fit, keeps its shape) -----
        imageBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                if (image != null) {
                    double scale = Math.min((double) getWidth() / image.getWidth(),
                                            (double) getHeight() / image.getHeight());
                    int w = (int) (image.getWidth() * scale);
                    int h = (int) (image.getHeight() * scale);
                    g2.drawImage(image, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);
                } else {
                    // placeholder while loading or when offline
                    g2.setColor(new Color(0xC9C8C4));
                    Font f = UITheme.font(Font.BOLD, 16f, 0.3f);
                    g2.setFont(f);
                    int tw = (int) f.getStringBounds("DAMNIT", g2.getFontRenderContext()).getWidth();
                    g2.drawString("DAMNIT", (getWidth() - tw) / 2, getHeight() / 2);
                }
                g2.dispose();
            }
        };
        imageBox.setBackground(IMAGE_BG);
        imageBox.setPreferredSize(new Dimension(200, 330));
 
        // ----- text -----
        JLabel category = UITheme.label(product.getCategory().toUpperCase(), Font.PLAIN, 10f, 0.2f, UITheme.GRAY);
        JLabel name = UITheme.label("<html><body style='width:190px'>" + escape(product.getName()) + "</body></html>",
                Font.BOLD, 13f, 0.05f, UITheme.BLACK);
        name.setVerticalAlignment(SwingConstants.TOP);
        name.setPreferredSize(new Dimension(190, 36));
        name.setMaximumSize(new Dimension(Short.MAX_VALUE, 36));
 
        JLabel price = UITheme.label(UITheme.money(product.getPrice()), Font.PLAIN, 13f, 0.05f, UITheme.BLACK);
        stock = UITheme.label(stockText(product), Font.PLAIN, 10f, 0.15f,
                product.getStock() <= 5 ? LOW_STOCK : UITheme.GRAY);
        JPanel priceRow = new JPanel(new BorderLayout());
        priceRow.setOpaque(false);
        priceRow.add(price, BorderLayout.WEST);
        priceRow.add(stock, BorderLayout.EAST);
 
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[]{category, name, priceRow}) {
            c.setAlignmentX(LEFT_ALIGNMENT);
        }
        text.add(category);
        text.add(Box.createVerticalStrut(4));
        text.add(name);
        text.add(Box.createVerticalStrut(6));
        text.add(priceRow);
 
        // ----- action: ADD button or SOLD OUT label (both built once; refreshStock() shows one) -----
        JLabel sold = UITheme.label("SOLD OUT", Font.BOLD, 12f, 0.2f, UITheme.GRAY);
        sold.setHorizontalAlignment(SwingConstants.CENTER);
        sold.setBorder(BorderFactory.createLineBorder(UITheme.GRAY));
        sold.setPreferredSize(new Dimension(10, 38));

        BlackButton add = new BlackButton("ADD TO ORDER");
        add.setFont(UITheme.font(Font.BOLD, 11f, 0.15f));
        add.addActionListener(e -> {
            onAdd.accept(product);
            refreshStock();              // the stock number follows what was just added
        });

        actionWrap.setOpaque(false);
        actionWrap.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        actionWrap.add(add, "add");
        actionWrap.add(sold, "sold");
        refreshStock();
 
        JPanel info = new JPanel(new BorderLayout());
        info.setOpaque(false);
        info.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        info.add(text, BorderLayout.NORTH);
        info.add(actionWrap, BorderLayout.SOUTH);
 
        add(imageBox, BorderLayout.CENTER);
        add(info, BorderLayout.SOUTH);
 
        // Load the picture in the background; repaint when it arrives
        ImageLoader.load(product.getId(), product.getImageUrl(), img -> {
            image = img;
            imageBox.repaint();
        });
    }
 

	/* Updates the stock text and swaps ADD TO ORDER for SOLD OUT when nothing is left. */
    public void refreshStock() {
        stock.setText(stockText(product));
        stock.setForeground(product.getStock() <= 5 ? LOW_STOCK : UITheme.GRAY);
        actionCards.show(actionWrap, product.getStock() <= 0 ? "sold" : "add");
    }

	private static String stockText(Product p) {
        if (p.getStock() <= 0) return "OUT OF STOCK";
        if (p.getStock() <= 5) return "ONLY " + p.getStock() + " LEFT";
        return p.getStock() + " IN STOCK";
    }
 
    private static String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

}