package dev.fastball.platform.core.exception;

import java.io.IOException;

/**
 * @author gr@fastball.dev
 * @since 2023/01/04
 */
public class GenerateException extends RuntimeException {
    public GenerateException(String message) {
        super(message);
    }

    public GenerateException(Throwable cause) {
        super(cause);
    }

    public GenerateException(String message, IOException cause) {
        super(message, cause);
    }
}
