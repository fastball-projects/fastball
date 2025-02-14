package dev.fastball.platform.core.utils;

import dev.fastball.compile.ComponentCompilerLoader;
import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.meta.component.ComponentInfo_AutoValue;
import dev.fastball.meta.utils.JsonUtils;
import dev.fastball.platform.core.exception.GenerateException;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2023/1/5
 */
public class ResourceUtils {

    private ResourceUtils() {
    }

    private static final Map<ClassLoader, ResourcePatternResolver> RESOLVER_MAP = new ConcurrentHashMap<>();

    public static Map<String, List<ComponentInfo<?>>> loadComponentInfoMap(ClassLoader classLoader) {
        List<ComponentInfo<?>> componentInfoList = loadComponentInfoList(classLoader);
        return componentInfoList.stream().collect(Collectors.groupingBy(componentInfo -> componentInfo.material().getPlatform()));
    }

    public static List<ComponentInfo<?>> loadComponentInfoList(ClassLoader classLoader) {
        ComponentCompilerLoader.registryComponentPropsType(classLoader);
        try {
            return Arrays.stream(getResourceResolver(classLoader).getResources("classpath*:/FASTBALL-INF/**/*.fbv.json"))
                    .map(resource -> {
                        try (InputStream inputStream = resource.getInputStream()) {
                            ComponentInfo<?> componentInfo = JsonUtils.fromJson(inputStream, ComponentInfo_AutoValue.class, classLoader);
                            return componentInfo;
                        } catch (IOException e) {
                            throw new GenerateException(e);
                        }
                    }).collect(Collectors.toList());
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }

    // TODO 这里依赖 Spring 不是很合适, 回头换成其他的 resource loader
    public static ResourcePatternResolver getResourceResolver(ClassLoader classLoader) {
        return RESOLVER_MAP.computeIfAbsent(classLoader, PathMatchingResourcePatternResolver::new);
    }

}
