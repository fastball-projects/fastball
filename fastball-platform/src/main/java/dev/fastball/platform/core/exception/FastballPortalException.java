package dev.fastball.platform.core.exception;

public class FastballPortalException extends RuntimeException {

    public FastballPortalException() {
    }

    public FastballPortalException(String message) {
        super(message);
    }

    public FastballPortalException(String message, Throwable cause) {
        super(message, cause);
    }

    public FastballPortalException(Throwable cause) {
        super(cause);
    }

    public FastballPortalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
