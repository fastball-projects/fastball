package dev.fastball.core.component;

import dev.fastball.core.info.ComponentInfo;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public interface ComponentCompiler<T extends Component, P> {
    ComponentInfo<P> compile(Class<T> componentClass);

    ComponentBean buildComponentBean(Component component);

    String getComponentKey(Class<? extends Component> componentClass);

    String getComponentName();

    boolean support(Class<?> componentClass);

}
