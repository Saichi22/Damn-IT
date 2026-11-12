package exception;

/*
 * Thrown when a login, sign-up or password-reset action fails
 * (invalid input, wrong credentials, duplicate username, etc.).
 */
public class AuthException extends Exception {
    public AuthException(String message) {
        super(message);
    }
}