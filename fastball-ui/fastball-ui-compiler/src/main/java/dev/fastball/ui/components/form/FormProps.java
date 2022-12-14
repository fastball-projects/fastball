package dev.fastball.ui.components.form;

import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.FieldInfo;
import lombok.Data;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
@Data
public class FormProps {
    String headerTitle;

    List<FieldInfo> fields;

    List<ActionInfo> actions;
}
