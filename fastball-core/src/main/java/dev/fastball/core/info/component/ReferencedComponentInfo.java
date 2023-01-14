package dev.fastball.core.info.component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.core.utils.RefComponentSerialize;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/18
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class ReferencedComponentInfo {
    private boolean defaultComponent = true;

    private String componentClass;

    private String componentPackage;

    private String componentPath;

    @JsonSerialize(using = RefComponentSerialize.class)
    private String component;
}
