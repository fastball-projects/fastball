package dev.fastball.platform;

import dev.fastball.core.GenericTypeGetter;
import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.meta.utils.YamlUtils;
import dev.fastball.platform.exception.GenerateException;
import dev.fastball.platform.utils.ResourceUtils;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import static dev.fastball.platform.FastballPlatformConstants.PLATFORM_CONFIG_PATH_PREFIX;
import static dev.fastball.platform.FastballPlatformConstants.PLATFORM_CONFIG_SUFFIX;

public interface FastballPlatform<T> extends GenericTypeGetter<T> {

    String platform();

    void generate(File workspaceDir, List<ComponentInfo<?>> componentInfoList, OutputStream consoleInfoOut, OutputStream consoleErrorOut);

    void run(File workspaceDir, List<ComponentInfo<?>> componentInfoList, PlatformDevServerConfig devServerConfig);

    void build(File workspaceDir, File targetDir, List<ComponentInfo<?>> componentInfoList, OutputStream consoleInfoOut, OutputStream consoleErrorOut);

    default T loadPlatformConfig() {
        Resource menuResource = ResourceUtils.getResourceResolver(getClass().getClassLoader()).getResource(PLATFORM_CONFIG_PATH_PREFIX + platform() + PLATFORM_CONFIG_SUFFIX);
        try (InputStream inputStream = menuResource.getInputStream()) {
            return YamlUtils.fromYaml(inputStream, getGenericTypeClass());
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }
}
