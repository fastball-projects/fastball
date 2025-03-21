package dev.fastball.platform;

import java.util.*;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public class FastballPlatformLoader {

    private final Map<String, FastballPlatform<?>> platformPortalMap = new HashMap<>();

    public static Set<FastballPlatform<?>> getPlatformPortal(String platform) {
        return getAllPlatformPortal(FastballPlatformLoader.class.getClassLoader());
    }

    public static Set<FastballPlatform<?>> getAllPlatformPortal(ClassLoader classLoader) {
        Set<FastballPlatform<?>> componentCompilers = new HashSet<>();
        for (FastballPlatform<?> componentCompiler : ServiceLoader.load(FastballPlatform.class, classLoader)) {
            componentCompilers.add(componentCompiler);
        }
        return componentCompilers;
    }

}
