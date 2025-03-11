package dev.fastball.components.form.metadata;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.components.form.config.FormLayout;
import dev.fastball.meta.annotation.PropertyDescription;
import dev.fastball.meta.basic.PopupType;
import dev.fastball.meta.component.ComponentProps;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
@AutoValue
public interface FormProps extends ComponentProps {
    @PropertyDescription("Form layout, default Vertical")
    FormLayout layout();

    @PropertyDescription("表单标题")
    String headerTitle();

    boolean variableForm();

    @PropertyDescription("Is a read-only form")
    boolean readonly();

    @PropertyDescription("Form Fields")
    List<? extends FormFieldInfo> fields();

    List<ValueChangeHandlerInfo> valueChangeHandlers();
}
