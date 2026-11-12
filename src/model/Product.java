package model;

public class Product {
	 private String id;
	    private String name;
	    private String category;
	    private double price;
	    private int stock;

	    public Product(String id, String name, String category, double price, int stock) {
	        this.id = id;
	        this.name = name;
	        this.category = category;
	        this.price = price;
	        this.stock = stock;
	    }

	    public String getId() { return id; }
	    public String getName() { return name; }
	    public String getCategory() { return category; }
	    public double getPrice() { return price; }
	    public int getStock() { return stock; }

	    public void setName(String name) { this.name = name; }
	    public void setCategory(String category) { this.category = category; }
	    public void setPrice(double price) { this.price = price; }
	    public void setStock(int stock) { this.stock = stock; }
}
