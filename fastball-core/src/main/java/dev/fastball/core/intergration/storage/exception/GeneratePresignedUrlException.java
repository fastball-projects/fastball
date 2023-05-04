package dev.fastball.core.intergration.storage.exception;

import dev.fastball.core.exception.FastballRuntimeException;

public class GeneratePresignedUrlException extends FastballRuntimeException {

    public GeneratePresignedUrlException() {
    }

    public GeneratePresignedUrlException(String message) {
        super(message);
    }

    public GeneratePresignedUrlException(String message, Throwable cause) {
        super(message, cause);
    }
}
