package dev.fastball.runtime.spring.devtools;

import dev.fastball.generate.exception.GenerateException;
import dev.fastball.generate.generator.PortalCodeGenerator;
import dev.fastball.generate.utils.ExecUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@Slf4j
public class DevServer implements WebMvcConfigurer, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final PortalCodeGenerator portalCodeGenerator = new DevModePortalCodeGenerator();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        Class<?> mainClass = getMainClass();
        String primarySourcePath = mainClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        File primarySourceFile = new File(primarySourcePath);
        if (primarySourceFile.isFile()) {
            throw new GenerateException("DevServer only available in development mode, but primary source [" + mainClass + "] in a jar.");
        }
        File generatedCodeDir = new File(primarySourceFile.getParentFile(), "fastball-workspace");
        portalCodeGenerator.generate(generatedCodeDir, mainClass.getClassLoader());
        OutputStream infoOut = new Slf4jLogOutputStream(log, Slf4jLogOutputStream.LogLevel.INFO);
        OutputStream errorOut = new Slf4jLogOutputStream(log, Slf4jLogOutputStream.LogLevel.ERROR);
        try {
            ExecUtils.checkNodeAndYarn();
            ExecUtils.exec("yarn", generatedCodeDir, infoOut, errorOut);
            ExecUtils.execAsync("yarn dev --open", generatedCodeDir, infoOut, errorOut);
        } catch (IOException e) {
            throw new GenerateException(e);
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
