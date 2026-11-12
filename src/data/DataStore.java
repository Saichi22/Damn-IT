package data;
import java.util.ArrayList;
import java.util.HashMap;
import model.Product;

public class DataStore {
	 // Hard-coded cashier accounts (username -> password)
    public static final HashMap<String, String> cashiers = new HashMap<>();
    public static final ArrayList<Product> products = new ArrayList<>();

    static {
        cashiers.put("cashier1", "1234");
        cashiers.put("admin", "admin123");

        products.add(new Product("P001", "Oversized Cotton Tee", "Tops", 450.0, 50));
        products.add(new Product("P002", "Relaxed Chino Pants", "Bottoms", 1200.0, 30));
        products.add(new Product("P003", "Heavyweight Pullover Hoodie", "Outerwear", 1500.0, 20));
        products.add(new Product("P004", "Canvas Tote Bag", "Accessories", 350.0, 40));
        products.add(new Product("P005", "Vintage Washed Cap", "Accessories", 400.0, 25));
    }

    public static boolean login(String user, String pass) {
        return cashiers.containsKey(user) && cashiers.get(user).equals(pass);
    }
}
