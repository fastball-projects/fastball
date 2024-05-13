package dev.fastball.meta.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupInfo {
    private String width;

    private String title;

    private PopupType popupType;

    private PopupTriggerType triggerType;

    private PlacementType placementType;

    private RefComponentInfo popupComponent;

    private boolean dynamicPopup;

    private String[] conditionPath;

    private List<DynamicPopupRuleInfo> dynamicPopupRules;
}
