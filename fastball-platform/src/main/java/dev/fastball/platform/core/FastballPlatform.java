package dev.fastball.platform.core;

import dev.fastball.core.GenericTypeGetter;
import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.meta.utils.YamlUtils;
import dev.fastball.platform.core.exception.GenerateException;
import dev.fastball.platform.core.utils.ResourceUtils;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static dev.fastball.platform.core.FastballPlatformConstants.PLATFORM_CONFIG_PATH_PREFIX;
import static dev.fastball.platform.core.FastballPlatformConstants.PLATFORM_CONFIG_SUFFIX;

public interface FastballPlatform<T> extends GenericTypeGetter<T> {

    String platform();

    void build(File workspaceDir, List<ComponentInfo<?>> componentInfoList);

    void run(File workspaceDir, List<ComponentInfo<?>> componentInfoList, OutputStream consoleInfoOut, OutputStream consoleErrorOut);

    default T loadPlatformConfig() {
        Resource menuResource = ResourceUtils.getResourceResolver(getClass().getClassLoader()).getResource(PLATFORM_CONFIG_PATH_PREFIX + platform() + PLATFORM_CONFIG_SUFFIX);
        try (InputStream inputStream = menuResource.getInputStream()) {
            return YamlUtils.fromYaml(inputStream, getGenericTypeClass());
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }
}
