package dev.fastball.platform.core.security.handler;

import dev.fastball.core.Result;
import dev.fastball.platform.core.security.utils.ResponseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@RequiredArgsConstructor
public class UnloginAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ResponseUtils responseUtils;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Result<?> result = new Result<>(401, authException.getMessage(), null);
        responseUtils.writeResult(response, result);
    }
}
