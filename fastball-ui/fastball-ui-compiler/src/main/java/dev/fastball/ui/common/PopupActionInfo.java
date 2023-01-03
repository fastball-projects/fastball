package dev.fastball.ui.common;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.core.component.ReferencedComponentInfo;
import dev.fastball.ui.ActionType;
import dev.fastball.ui.DrawerPlacementType;
import dev.fastball.ui.PopupType;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
public interface PopupActionInfo extends ActionInfo {
    ActionType type = ActionType.Popup;

    String popupTitle();

    PopupType popupType();

    DrawerPlacementType drawerPlacementType();

    ReferencedComponentInfo popupComponent();
}