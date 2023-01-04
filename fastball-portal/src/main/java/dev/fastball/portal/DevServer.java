package dev.fastball.portal;

import dev.fastball.core.component.ComponentInfo;
import dev.fastball.core.component.ComponentInfo_AutoValue;
import dev.fastball.core.config.FastballConfig;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.core.utils.YamlUtils;
import dev.fastball.generate.generator.PortalCodeGenerator;
import org.apache.commons.io.IOUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
public class DevServer implements WebMvcConfigurer, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Class<?> mainClass = getMainClass();
        String primarySourcePath = mainClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        File primarySourceFile = new File(primarySourcePath);
        if (primarySourceFile.isFile()) {
            throw new RuntimeException("DevServer only available in development mode, but primary source in a jar.");
        }
        File generatedCodeDir = new File(primarySourceFile.getParentFile(), "fastball-workspace");
        PortalCodeGenerator.generate(generatedCodeDir, mainClass.getClassLoader());
        try {
            Runtime.getRuntime().exec("pnpm i", null, generatedCodeDir).waitFor();
            new Thread(() -> {
                Process devProcess;
                try {
                    devProcess = Runtime.getRuntime().exec("pnpm run dev", null, generatedCodeDir);
                    IOUtils.copy(devProcess.getInputStream(), System.out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
//            Runtime.getRuntime().exec("pnpm run build", null, generatedCodeDir).waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
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
