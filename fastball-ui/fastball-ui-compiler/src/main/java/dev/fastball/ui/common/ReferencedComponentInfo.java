package dev.fastball.ui.common;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.core.component.Component;

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
}
