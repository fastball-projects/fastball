package dev.fastball.maven.generator;

import dev.fastball.core.component.ComponentInfo;
import dev.fastball.core.material.MaterialRegistry;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.model.Resource;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static dev.fastball.maven.Constants.FASTBALL_RESOURCE_PREFIX;
import static dev.fastball.maven.Constants.FASTBALL_VIEW_SUFFIX;

public class ViewGenerator {
    private ViewGenerator() {
    }

    public static List<ComponentInfo<?>> generate(MavenProject project, MaterialRegistry materialRegistry, ClassLoader projectClassLoader, boolean force) {
        List<ComponentInfo<?>> componentInfoList = new ArrayList<>();
        File moduleOutputDir = new File(project.getBuild().getOutputDirectory());
        List<Resource> resources = project.getResources();
        if (resources == null || resources.isEmpty()) {
            return componentInfoList;
        }
        File resourceDir = new File(resources.get(0).getDirectory(), FASTBALL_RESOURCE_PREFIX);
        return componentInfoList;
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
