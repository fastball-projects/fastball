package dev.fastball.compile.utils;

import dev.fastball.core.info.basic.ValidationRuleInfo;
import dev.fastball.core.info.basic.ValidationRuleInfo_AutoValue;

import javax.lang.model.element.VariableElement;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class ValidationCompileUtils {

    private ValidationCompileUtils() {
    }

    public static List<ValidationRuleInfo> compileFieldJsr303(VariableElement field) {
        List<ValidationRuleInfo> validationRules = new ArrayList<>();
        Min min = field.getAnnotation(Min.class);
        if (min != null) {
            validationRules.add(ValidationRuleInfo_AutoValue.builder().type("number").min(min.value()).message(min.message()).build());
        }
        Max max = field.getAnnotation(Max.class);
        if (max != null) {
            validationRules.add(ValidationRuleInfo_AutoValue.builder().type("number").max(max.value()).message(max.message()).build());
        }
        Size size = field.getAnnotation(Size.class);
        if (size != null) {
            validationRules.add(ValidationRuleInfo_AutoValue.builder().type("string").min(size.min()).max(size.max()).message(size.message()).build());
        }
        NotNull notNull = field.getAnnotation(NotNull.class);
        if (notNull != null) {
            validationRules.add(ValidationRuleInfo_AutoValue.builder().required(true).message(notNull.message()).build());
        }
        Pattern pattern = field.getAnnotation(Pattern.class);
        if (pattern != null) {
            validationRules.add(ValidationRuleInfo_AutoValue.builder().pattern(pattern.regexp()).message(pattern.message()).build());
        }
        return validationRules;
    }
}
