package dev.fastball.ui.components.form;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.ui.PopupType;
import dev.fastball.ui.common.ActionInfo;
import dev.fastball.ui.common.ComponentProps;
import dev.fastball.ui.common.FieldInfo;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2022/12/14
 */
@AutoValue
public interface FormProps extends ComponentProps {
    String headerTitle();

    PopupType popupType();

    List<FieldInfo> fields();

    List<ActionInfo> actions();
}
