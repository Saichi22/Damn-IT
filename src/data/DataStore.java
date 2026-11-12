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

        products.add(new Product("P001", "Americano", "Coffee", 90.0, 50));
        products.add(new Product("P002", "Cafe Latte", "Coffee", 110.0, 50));
        products.add(new Product("P003", "Blueberry Muffin", "Pastry", 75.0, 20));
    }

    public static boolean login(String user, String pass) {
        return cashiers.containsKey(user) && cashiers.get(user).equals(pass);
    }
}
