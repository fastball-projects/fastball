package dev.fastball.platform.security.handler;

import dev.fastball.core.Result;
import dev.fastball.platform.security.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class FastballAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ResponseUtils responseUtils;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        responseUtils.writeResult(response, Result.fail(exception.getMessage()));
    }
}