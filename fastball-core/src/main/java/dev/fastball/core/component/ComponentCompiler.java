package dev.fastball.core.component;

import dev.fastball.core.info.ComponentInfo;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
public interface ComponentCompiler<T, P> {
    ComponentInfo<P> compile(Class<T> componentClass);

    boolean support(Class<?> componentClass);

}
