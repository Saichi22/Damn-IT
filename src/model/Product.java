package model;

public class Product {
    private String id;
    private String name;
    private String category;
    private String section;
    private double price;
    private int stock;
    private String imageUrl;

    public Product(String id, String name, String category, String section, double price, int stock, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.section = section;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
    }

    public Product(String id, String name, String category, double price, int stock) {
        this(id, name, category, "UNISEX", price, stock, "");
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getSection() { return section; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
    public String getImageUrl() { return imageUrl; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setSection(String section) { this.section = section; }
    public void setPrice(double price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}