package dev.fastball.meta.component;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.meta.material.UIMaterial;
import dev.fastball.meta.utils.ComponentInfoDeserializer;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
@JsonDeserialize(using = ComponentInfoDeserializer.class)
public interface ComponentInfo<T extends ComponentProps> {
    String componentKey();

    String componentName();

    String componentPath();

    String className();

    UIMaterial material();

    Boolean customized();

    String propsClassName();

    T props();
}
