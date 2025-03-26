package dev.fastball.runtime.spring.devtools;

import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.platform.PlatformDevServerConfig;
import dev.fastball.platform.FastballPlatform;
import dev.fastball.platform.FastballPlatformLoader;
import dev.fastball.platform.exception.FastballPortalException;
import dev.fastball.platform.exception.GenerateException;
import dev.fastball.platform.utils.ResourceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
public class DevServer implements WebMvcConfigurer, InitializingBean {

    private final FastballDevServerProperties devServerProperties;

    @Override
    public void afterPropertiesSet() {
        Class<?> mainClass = getMainClass();
        ClassLoader classLoader = mainClass.getClassLoader();
        String primarySourcePath = mainClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        File primarySourceFile = new File(primarySourcePath);
        if (primarySourceFile.isFile()) {
            throw new GenerateException("DevServer only available in development mode, but primary source [" + mainClass + "] in a jar.");
        }

        Map<String, List<ComponentInfo<?>>> componentPlatformGroup = ResourceUtils.loadComponentInfoMap(classLoader);

        File workspaceDir = new File(primarySourceFile.getParentFile(), "fastball-workspace");

        Set<FastballPlatform<?>> platformSet = FastballPlatformLoader.getAllPlatformPortal(classLoader);

        Map<String, PlatformDevServerConfig> devServerPropertiesMap;
        if (devServerProperties != null && devServerProperties.getDevServer() != null) {
            devServerPropertiesMap = devServerProperties.getDevServer();
        } else {
            devServerPropertiesMap = Map.of();
        }

        try (OutputStream consoleInfoOut = new Slf4jLogOutputStream(log, Slf4jLogOutputStream.LogLevel.INFO)) {
            for (FastballPlatform<?> fastballPlatform : platformSet) {
                File platformWorkspace = new File(workspaceDir, fastballPlatform.platform());
                PlatformDevServerConfig devServerConfig = devServerPropertiesMap.get(fastballPlatform.platform());
                List<ComponentInfo<?>> components = componentPlatformGroup.get(fastballPlatform.platform());
                String threadName = "fastball-platform-" + fastballPlatform.platform();
                new Thread(() -> fastballPlatform.run(platformWorkspace, components, devServerConfig, consoleInfoOut), threadName).start();
            }
        } catch (Exception e) {
            throw new FastballPortalException("DevServer start failed", e);
        }
    }

    private Class<?> getMainClass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement main = stack[stack.length - 1];
        try {
            return Class.forName(main.getClassName());
        } catch (ClassNotFoundException ignore) {
            throw new IllegalStateException("Cannot determine main class.");
        }
    }
}
