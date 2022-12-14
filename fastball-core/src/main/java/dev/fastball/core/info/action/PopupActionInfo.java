package dev.fastball.core.info.action;

import dev.fastball.core.info.basic.PlacementType;
import dev.fastball.core.info.basic.PopupType;
import dev.fastball.core.info.component.ReferencedComponentInfo;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
public class PopupActionInfo extends ActionInfo {
    protected ActionType type = ActionType.Popup;

    private Integer width;

    private String popupTitle;

    private PopupType popupType;

    private PlacementType placementType;

    private ReferencedComponentInfo popupComponent;
}