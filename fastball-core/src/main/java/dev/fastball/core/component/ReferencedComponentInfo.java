package dev.fastball.core.component;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.core.RefComponentSerialize;

/**
 * @author gr@fastball.dev
 * @since 2022/12/18
 */
@AutoValue
@JsonDeserialize(as = ReferencedComponentInfo_AutoValue.class)
public interface ReferencedComponentInfo {
    String componentClass();

    String componentPackage();

    String componentPath();

    String componentName();

    @JsonSerialize(using = RefComponentSerialize.class)
    String component();

    void component(String component);
}
