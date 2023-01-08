package dev.fastball.generate.utils;

import dev.fastball.core.config.FastballConfig;
import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.info.component.ComponentInfo_AutoValue;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.core.utils.YamlUtils;
import dev.fastball.generate.exception.GenerateException;
import org.springframework.core.io.Resource;
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

    public static FastballConfig loadFastballConfig(ClassLoader classLoader) {
        Resource menuResource = getResourceResolver(classLoader).getResource("classpath:/fastball-config.yml");
        try (InputStream inputStream = menuResource.getInputStream()) {
            return YamlUtils.fromYaml(inputStream, FastballConfig.class);
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }

    public static List<ComponentInfo<?>> loadComponentInfoList(ClassLoader classLoader) {
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
    private static ResourcePatternResolver getResourceResolver(ClassLoader classLoader) {
        return RESOLVER_MAP.computeIfAbsent(classLoader, PathMatchingResourcePatternResolver::new);
    }

}
