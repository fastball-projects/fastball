package dev.fastball.core.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class ComponentRegistry {

    private final Map<String, Component> componentMap = new ConcurrentHashMap<>();
}
