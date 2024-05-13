package dev.fastball.core.annotation;

import dev.fastball.core.component.AutoCompleteLookupAction;
import dev.fastball.meta.basic.AutoCompleteInputType;

import java.lang.annotation.*;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoComplete {
    Class<? extends AutoCompleteLookupAction> value();

    AutoCompleteInputType inputType() default AutoCompleteInputType.Text;

    String valueField();

    DisplayField[] displayFields();

    String[] dependencyFields() default {};

    FillField[] extraFillFields() default {};

    @Target({})
    @Retention(RetentionPolicy.RUNTIME)
    @interface DisplayField {
        String name();

        String title();
    }
}
