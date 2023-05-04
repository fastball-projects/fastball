package dev.fastball.core.intergration.storage.exception;

import dev.fastball.core.exception.FastballRuntimeException;

public class GetObjectException extends FastballRuntimeException {

    public GetObjectException() {
    }

    public GetObjectException(String message) {
        super(message);
    }

    public GetObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
