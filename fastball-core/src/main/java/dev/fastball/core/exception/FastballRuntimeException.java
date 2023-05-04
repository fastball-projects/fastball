package dev.fastball.core.exception;

public class FastballRuntimeException extends RuntimeException {

    public FastballRuntimeException() {
    }

    public FastballRuntimeException(String message) {
        super(message);
    }

    public FastballRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FastballRuntimeException(Throwable cause) {
        super(cause);
    }

    public FastballRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
