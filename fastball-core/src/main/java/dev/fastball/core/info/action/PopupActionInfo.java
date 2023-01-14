package dev.fastball.core.info.action;

import dev.fastball.core.info.basic.PlacementType;
import dev.fastball.core.info.basic.PopupType;
import dev.fastball.core.info.component.ReferencedComponentInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PopupActionInfo extends ActionInfo {
    protected final ActionType type = ActionType.Popup;

    private Integer width;

    private String popupTitle;

    private PopupType popupType;

    private PlacementType placementType;

    private ReferencedComponentInfo popupComponent;
}