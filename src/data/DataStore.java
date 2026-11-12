package data;

import java.util.ArrayList;
import java.util.HashMap;
import exception.AuthException;
import model.Account;
import model.Product;

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

    static {
        accounts.put("admin", new Account("admin", "admin123", "Store Admin",
                SECURITY_QUESTIONS[0], "bella"));
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