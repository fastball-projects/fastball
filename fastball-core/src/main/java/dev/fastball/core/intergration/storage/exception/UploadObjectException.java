package dev.fastball.core.intergration.storage.exception;

import dev.fastball.core.exception.FastballRuntimeException;

public class UploadObjectException extends FastballRuntimeException {

    public UploadObjectException() {
    }

    public UploadObjectException(String message) {
        super(message);
    }

    public UploadObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
