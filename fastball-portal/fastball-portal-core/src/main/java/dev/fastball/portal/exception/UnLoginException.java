package dev.fastball.portal.exception;

import org.springframework.security.core.AuthenticationException;

public class UnLoginException extends AuthenticationException {
    public UnLoginException() {
        super("未登录");
    }
}
