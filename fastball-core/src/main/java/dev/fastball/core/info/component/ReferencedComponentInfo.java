package dev.fastball.core.info.component;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/18
 */
@Data
public class ReferencedComponentInfo {
    private String componentClass;

    private String componentPackage;

    private String componentPath;

    private String componentName;

    @JsonSerialize(using = RefComponentSerialize.class)
    private String component;
}
