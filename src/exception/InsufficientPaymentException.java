package exception;

public class InsufficientPaymentException extends Exception{
	 private static final long serialVersionUID = 1L;
	 public InsufficientPaymentException(String message) {
	        super(message);
	    }
}
