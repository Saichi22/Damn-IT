package exception;

public class InsufficientPaymentException extends Exception {
    private static final long serialVersionUID = 1L;
    private final double shortfall;

    public InsufficientPaymentException(String message, double shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public double getShortfall() { return shortfall; }
}