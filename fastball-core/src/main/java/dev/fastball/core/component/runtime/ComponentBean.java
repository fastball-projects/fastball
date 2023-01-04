package dev.fastball.core.component.runtime;

import dev.fastball.core.component.Component;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
@Data
public class ComponentBean {

    private String componentKey;
    private Component component;
    private Map<String, Method> methodMap;
}
