package dev.fastball.core.component;

import java.util.*;

/**
 * @author gengrong
 */
public class ComponentCompilerLoader {
    private static final Set<ComponentCompiler<?, ?>> COMPONENT_COMPILERS = new HashSet<>();

    static {
        for (ComponentCompiler<?, ?> componentCompiler : ServiceLoader.load(ComponentCompiler.class)) {
            COMPONENT_COMPILERS.add(componentCompiler);
        }
    }

    public static Set<ComponentCompiler<?, ?>> getLoaders() {
        return COMPONENT_COMPILERS;
    }

    public static Set<ComponentCompiler<?, ?>> getLoaders(ClassLoader classLoader) {
        if (COMPONENT_COMPILERS.isEmpty()) {
            for (ComponentCompiler<?, ?> componentCompiler : ServiceLoader.load(ComponentCompiler.class, classLoader)) {
                COMPONENT_COMPILERS.add(componentCompiler);
            }
        }
        return COMPONENT_COMPILERS;
    }
}
