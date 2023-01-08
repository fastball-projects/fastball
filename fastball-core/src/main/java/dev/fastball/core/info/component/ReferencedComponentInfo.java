package dev.fastball.core.info.component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.auto.value.annotation.AutoValue;

/**
 * @author gr@fastball.dev
 * @since 2022/12/18
 */
@AutoValue
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface ReferencedComponentInfo {
    String componentClass();

    String componentPackage();

    String componentPath();

    String componentName();

    @JsonSerialize(using = RefComponentSerialize.class)
    String component();

    void component(String component);
}
