package dev.fastball.maven.generator;

import dev.fastball.core.annotation.UIComponent;
import dev.fastball.core.component.ComponentCompiler;
import dev.fastball.core.component.ComponentCompilerLoader;
import dev.fastball.core.info.ComponentInfo;
import dev.fastball.maven.JsonUtils;
import dev.fastball.maven.material.MaterialRegistry;
import dev.fastball.core.info.UIMaterial;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static dev.fastball.maven.Constants.FASTBALL_RESOURCE_PREFIX;
import static dev.fastball.maven.Constants.FASTBALL_VIEW_SUFFIX;

public class ViewGenerator {
    private ViewGenerator() {
    }

    public static void generate(MavenProject project, ScanResult scanResult, ClassLoader projectClassLoader, boolean force) {
        File moduleOutputDir = new File(project.getBuild().getOutputDirectory());
        List<Resource> resources = project.getResources();
        if (resources == null || resources.isEmpty()) {
            return;
        }
        File resourceDir = new File(resources.get(0).getDirectory(), FASTBALL_RESOURCE_PREFIX);
        MaterialRegistry materialRegistry = new MaterialRegistry(projectClassLoader);
        ClassInfoList componentClassList = scanResult.getClassesWithAnnotation(UIComponent.class);
        for (Class<?> componentClass : componentClassList.getStandardClasses().loadClasses()) {
            File codeSource = new File(componentClass.getProtectionDomain().getCodeSource().getLocation().getPath());
            if (!moduleOutputDir.equals(codeSource)) {
                continue;
            }
            for (ComponentCompiler<?, ?> compiler : ComponentCompilerLoader.getLoaders(projectClassLoader)) {
                if (compiler.support(componentClass)) {
                    File loaderSource = new File(compiler.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
                    File viewFile = buildViewFile(resourceDir, componentClass);
                    if (!force && viewFile.exists()) {
                        continue;
                    }
                    try {
                        ComponentInfo<?> componentInfo = compiler.compile((Class) componentClass);
                        UIMaterial material = materialRegistry.getMaterial(loaderSource.getAbsolutePath());
                        if (material != null) {
                            componentInfo.setMaterial(material);
                        }
                        String code = JsonUtils.toPrettyJson(componentInfo);
                        FileUtils.write(viewFile, code, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private static File buildViewFile(File ruleDir, Class<?> componentClass) {
        String packageName = componentClass.getPackage().getName();
        String componentName = componentClass.getSimpleName();
        File parent = ruleDir;
        for (String path : StringUtils.split(packageName, ".")) {
            parent = new File(parent, path);
        }
        return new File(parent, componentName + FASTBALL_VIEW_SUFFIX);
    }
}
