package dev.fastball.core.info;

import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
public class ComponentInfo<T> {
    private String componentKey;
    private String componentName;
    private String className;
    private UIMaterial material;
    private T props;
}
