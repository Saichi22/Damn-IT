package data;

import java.util.ArrayList;
import java.util.HashMap;
import exception.AuthException;
import exception.InsufficientPaymentException;
import exception.OutOfStockException;
import model.Account;
import model.Order;
import model.Product;
import model.Sale;

/*
 * In-memory "database". Everything resets when the program closes.
 * Replace the sample products with your own team's products.
 */
public class DataStore {

    public static final String[] SECURITY_QUESTIONS = {
        "What is your pet's name?",
        "What city were you born in?",
        "What is your mother's maiden name?",
        "What was your first school?"
    };

    // username (lowercase) -> Account
    public static final HashMap<String, Account> accounts = new HashMap<>();
    public static final ArrayList<Product> products = new ArrayList<>();
    public static final ArrayList<Sale> sales = new ArrayList<>();   // every completed sale (for the daily report)
    private static int receiptCounter = 1;

    static {
        accounts.put("admin", new Account("admin", "admin123", "Store Admin",
                SECURITY_QUESTIONS[0], "bella", true));
        accounts.put("cashier1", new Account("cashier1", "1234567", "Cashier One",
                SECURITY_QUESTIONS[1], "manila"));
        products.addAll(ProductCatalog.create());
    }

    /* Returns the account if credentials are correct, otherwise throws. */
    public static Account login(String username, String password) throws AuthException {
        Account acc = accounts.get(username.toLowerCase());
        if (acc == null || !acc.getPassword().equals(password)) {
            throw new AuthException("Invalid username or password.");
        }
        return acc;
    }

    /*
     * Completes a sale in one step:
     *   1) validates the inputs, 2) re-checks stock, 3) checks the payment covers the total,
     *   4) builds the Sale record, 5) saves the sale (stock was already lowered by Order).
     * Nothing is changed if a check fails. The caller clears the order afterwards.
     */
    public static synchronized Sale completeSale(Order order, double tendered, Account cashier)
            throws OutOfStockException, InsufficientPaymentException {
        if (order == null || cashier == null) {
            throw new IllegalArgumentException("Missing order or cashier.");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("The order is empty.");
        }
        if (Double.isNaN(tendered) || Double.isInfinite(tendered) || tendered <= 0) {
            throw new IllegalArgumentException("Amount received must be a positive number.");
        }
        order.validateStock();

        long due = Math.round(order.getTotal() * 100);
        long paid = Math.round(tendered * 100);
        if (paid < due) {
            double shortBy = (due - paid) / 100.0;
            throw new InsufficientPaymentException(
                    String.format("Payment is short by \u20B1%,.2f.", shortBy), shortBy);
        }

        // Build the record first: if this fails, stock is untouched.
        Sale sale = new Sale(String.format("R%04d", receiptCounter), cashier.getFullName(), order, tendered);

        // Stock was already taken out when each item was added to the order,
        // so it is NOT lowered again here.
        receiptCounter++;      // only consumed once the sale really happened
        sales.add(sale);
        return sale;
    }

    public static void register(Account account) throws AuthException {
        String key = account.getUsername().toLowerCase();
        if (accounts.containsKey(key)) {
            throw new AuthException("That username is already taken. Choose another one.");
        }
        accounts.put(key, account);
    }

    public static void resetPassword(String username, String question,
                                     String answer, String newPassword) throws AuthException {
        Account acc = accounts.get(username.toLowerCase());
        if (acc == null
                || !acc.getSecurityQuestion().equals(question)
                || !acc.checkAnswer(answer)) {
            throw new AuthException("We could not verify your details. Check your username, question and answer.");
        }
        acc.setPassword(newPassword);
    }
}