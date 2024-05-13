package dev.fastball.meta.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gr@fastball.dev
 * @since 2024/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicPopupRuleInfo {

    private String[] values;

    private RefComponentInfo popupComponent;
}
