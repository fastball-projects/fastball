package dev.fastball.core.info.component;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.core.material.UIMaterial;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
public interface ComponentInfo<T extends ComponentProps> {
    String componentKey();

    String componentName();

    String className();

    UIMaterial material();

    Boolean customized();

    T props();
}
