package dev.fastball.runtime.spring;

import dev.fastball.core.Result;
import dev.fastball.core.exception.BusinessException;
import dev.fastball.core.exception.FieldValidationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class FastballExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<?> exceptionHandler(Exception e) {
        if (e instanceof FieldValidationException) {
            return Result.fail(((FieldValidationException) e).getFieldValidationMessages());
        }
        e.printStackTrace();
        if (e instanceof BusinessException) {
            return Result.fail(e.getMessage());
        }
        return Result.fail(e.getMessage());
    }
}


