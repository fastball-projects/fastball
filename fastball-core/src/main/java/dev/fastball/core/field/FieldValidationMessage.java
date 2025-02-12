package dev.fastball.core.field;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FieldValidationMessage {
    private List<String> fieldIndex;
    private List<String> errorMessages;

    public FieldValidationMessage(String field, String errorMessage) {
        this(List.of(field), errorMessage);
    }

    public FieldValidationMessage(String field, List<String> errorMessages) {
        this(List.of(field), errorMessages);
    }

    public FieldValidationMessage(List<String> fieldIndex, String errorMessage) {
        this(fieldIndex, List.of(errorMessage));
    }

    public FieldValidationMessage(List<String> fieldIndex, List<String> errorMessages) {
        this.fieldIndex = fieldIndex;
        this.errorMessages = errorMessages;
    }
}
