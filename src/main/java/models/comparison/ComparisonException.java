package models.comparison;

public class ComparisonException extends RuntimeException {
    public ComparisonException(String message) {
        super(message);
    }

    public ComparisonException(String message, Throwable cause) {
        super(message, cause);
    }
}