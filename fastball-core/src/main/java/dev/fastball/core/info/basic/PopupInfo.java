package dev.fastball.core.info.basic;

import dev.fastball.core.info.component.ReferencedComponentInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gr@fastball.dev
 * @since 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupInfo {
    private Integer width;

    private String popupTitle;

    private PopupType popupType;

    private PlacementType placementType;

    private ReferencedComponentInfo popupComponent;
}
