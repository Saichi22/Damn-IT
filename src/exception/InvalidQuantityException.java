package exception;

/*
 * Thrown when a quantity is not acceptable (zero, negative, empty, not a number).
 */
public class InvalidQuantityException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidQuantityException(String message) {
        super(message);
    }
}
