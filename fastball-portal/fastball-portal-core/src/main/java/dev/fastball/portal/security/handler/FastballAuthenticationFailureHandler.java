package dev.fastball.portal.security.handler;

import dev.fastball.core.Result;
import dev.fastball.portal.security.utils.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class FastballAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ResponseUtils responseUtils;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        responseUtils.writeResult(response, Result.fail(exception.getMessage()));
    }
}