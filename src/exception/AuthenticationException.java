package exception;

/**
 * Thrown when authentication fails due to invalid credentials
 * or when an operation is attempted without being logged in.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
