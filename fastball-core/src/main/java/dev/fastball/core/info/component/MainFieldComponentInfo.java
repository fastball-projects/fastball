package dev.fastball.core.info.component;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gr@fastball.dev
 * @since 2023/1/11
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class MainFieldComponentInfo extends ReferencedComponentInfo {
    private String[] mainField;
}
