package dev.fastball.compile.exception;

import java.io.IOException;

/**
 * @author gr@fastball.dev
 * @since 2022/12/19
 */
public class CompilerException extends RuntimeException {
    public CompilerException(String message) {
        super(message);
    }

    public CompilerException(Throwable cause) {
        super(cause);
    }
}
