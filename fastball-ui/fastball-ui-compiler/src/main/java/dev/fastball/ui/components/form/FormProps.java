package dev.fastball.ui.components.form;

import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.ComponentProps;
import dev.fastball.ui.common.FieldInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FormProps extends ComponentProps {
    String headerTitle;

    List<FieldInfo> fields;

    List<ActionInfo> actions;
}
