package dev.fastball.meta.component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.meta.utils.RefComponentSerialize;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/18
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "___class")
public class ReferencedComponentInfo {
    private boolean defaultComponent = true;

    private String componentClass;

    private String componentPackage;

    private String componentPath;

    @JsonSerialize(using = RefComponentSerialize.class)
    private String component;
}
