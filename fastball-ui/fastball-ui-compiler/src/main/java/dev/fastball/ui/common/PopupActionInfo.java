package dev.fastball.ui.common;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.ui.ActionType;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
public interface PopupActionInfo extends ActionInfo {
    ActionType type = ActionType.Popup;

    ReferencedComponentInfo popupComponent();
}