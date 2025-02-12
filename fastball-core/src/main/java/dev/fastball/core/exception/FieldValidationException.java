package dev.fastball.core.exception;

import dev.fastball.core.field.FieldValidationMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class FieldValidationException extends FastballRuntimeException {

    private final List<FieldValidationMessage> fieldValidationMessages;

    public FieldValidationException(String fieldName, String message) {
        this(new FieldValidationMessage(fieldName, message));
    }

    public FieldValidationException(FieldValidationMessage fieldValidationMessage) {
        this(List.of(fieldValidationMessage));
    }

    public FieldValidationException(List<FieldValidationMessage> fieldValidationMessages) {
        this.fieldValidationMessages = fieldValidationMessages;
    }

}
