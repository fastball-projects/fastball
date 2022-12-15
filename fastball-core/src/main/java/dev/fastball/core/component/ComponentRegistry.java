package dev.fastball.core.component;

import dev.fastball.core.component.Component;
import dev.fastball.core.component.ComponentBean;
import dev.fastball.core.component.ComponentCompiler;
import dev.fastball.core.component.ComponentCompilerLoader;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public class ComponentRegistry {

    private final Map<String, ComponentBean> componentBeanMap = new ConcurrentHashMap<>();

    public void register(Component component) {
        Class<? extends Component> componentClass = component.getClass();
        for (ComponentCompiler<?, ?> compiler : ComponentCompilerLoader.getLoaders()) {
            if (compiler.support(componentClass)) {
                ComponentBean componentBean = compiler.buildComponentBean(component);
                String componentKey = compiler.getComponentKey(component.getClass());
                componentBeanMap.put(componentKey, componentBean);
            }
        }
    }

    public ComponentBean getComponentBean(String componentKey) {
        return componentBeanMap.get(componentKey);
    }
}
