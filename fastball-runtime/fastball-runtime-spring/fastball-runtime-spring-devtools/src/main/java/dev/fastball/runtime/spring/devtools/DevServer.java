package dev.fastball.runtime.spring.devtools;

import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.platform.FastballPlatform;
import dev.fastball.platform.FastballPlatformLoader;
import dev.fastball.platform.exception.GenerateException;
import dev.fastball.platform.utils.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@Slf4j
public class DevServer implements WebMvcConfigurer, InitializingBean {

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

        OutputStream infoOut = new Slf4jLogOutputStream(log, Slf4jLogOutputStream.LogLevel.INFO);
        OutputStream errorOut = new Slf4jLogOutputStream(log, Slf4jLogOutputStream.LogLevel.ERROR);

        for (FastballPlatform<?> fastballPlatform : FastballPlatformLoader.getAllPlatformPortal(classLoader)) {
            File platformWorkspaceDir = new File(workspaceDir, fastballPlatform.platform());
            fastballPlatform.run(platformWorkspaceDir, componentPlatformGroup.get(fastballPlatform.platform()), infoOut, errorOut);
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
