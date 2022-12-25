package dev.fastball.ui.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.core.component.Component;
import dev.fastball.ui.util.RefComponentSerialize;

/**
 * @author gr@fastball.dev
 * @since 2022/12/18
 */
@AutoValue
public interface ReferencedComponentInfo {
    Class<? extends Component> componentClass();

    String componentPackage();

    String componentPath();

    String componentName();

    @JsonSerialize(using = RefComponentSerialize.class)
    String component();

    void component(String component);
}
