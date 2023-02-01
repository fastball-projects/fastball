package dev.fastball.core.component.runtime;

import dev.fastball.core.component.Component;
import dev.fastball.core.component.RecordActionFilter;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gr@fastball.dev
 * @since 2022/12/15
 */
@Data
public class ComponentBean {

    private String componentKey;
    private Component component;
    private Map<String, UIApiMethod> methodMap;
    private Map<String, Class<? extends RecordActionFilter>> recordActionFilterClasses;
    private Map<Class<? extends RecordActionFilter>, RecordActionFilter> recordActionFilters = new ConcurrentHashMap<>();
}
