package dev.fastball.compile;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public class ComponentCompilerLoader {


    public static Set<ComponentCompiler<?>> getLoaders() {
        return getLoaders(ComponentCompilerLoader.class.getClassLoader());
    }

    public static Set<ComponentCompiler<?>> getLoaders(ClassLoader classLoader) {
        Set<ComponentCompiler<?>> componentCompilers = new HashSet<>();
        for (ComponentCompiler<?> componentCompiler : ServiceLoader.load(ComponentCompiler.class, classLoader)) {
            componentCompilers.add(componentCompiler);
        }
        return componentCompilers;
    }
}
