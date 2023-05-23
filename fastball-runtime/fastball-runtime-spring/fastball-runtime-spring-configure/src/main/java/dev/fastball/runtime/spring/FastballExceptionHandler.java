package dev.fastball.runtime.spring;

import dev.fastball.core.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class FastballExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<?> exceptionHandler(Exception e) {
        return Result.fail(e.getMessage());
    }
}


