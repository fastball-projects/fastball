package dev.fastball.meta.utils;

import dev.fastball.meta.component.ComponentProps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gr@fastball.dev
 * @since 2024/5/13
 */
public interface ComponentPropsTypeRegistry {

    Map<String, Class<? extends ComponentProps>> componentPropsClassMap = new ConcurrentHashMap<>();

    static void register(String componentName, Class<? extends ComponentProps> componentPropsClass) {
        componentPropsClassMap.put(componentName, componentPropsClass);
    }

    static Class<? extends ComponentProps> get(String componentName) {
        return componentPropsClassMap.get(componentName);
    }
}
