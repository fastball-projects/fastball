package dev.fastball.maven;

import dev.fastball.core.component.ComponentInfo;
import dev.fastball.maven.generator.CodeGenerator;
import dev.fastball.maven.generator.ViewGenerator;
import dev.fastball.core.material.MaterialRegistry;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @author gengrong
 */
@Mojo(name = "publish", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class FrontendComponentMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "fastball.force", defaultValue = "false")
    private boolean force;

    @Parameter(property = "fastball.generate.view", defaultValue = "true")
    private boolean viewGenerate;

    @Parameter(property = "fastball.generate.code", defaultValue = "false")
    private boolean codeGenerate;

    @Override
    public void execute() {
        ClassLoader projectClassLoader = getClassLoader();

        if (viewGenerate) {
            MaterialRegistry materialRegistry = new MaterialRegistry(projectClassLoader);
            List<ComponentInfo<?>> componentInfoList = ViewGenerator.generate(project, materialRegistry, projectClassLoader, force);
            if (codeGenerate) {
                CodeGenerator.generate(project, materialRegistry, componentInfoList);
            }
        }

    }

    private ClassLoader getClassLoader() {
        try {
            List<String> classpathElements = project.getCompileClasspathElements();
            classpathElements.add(project.getBuild().getOutputDirectory());
            // 转为 URL 数组
            URL[] urls = new URL[classpathElements.size()];
            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();
            }
            // 自定义类加载器
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (DependencyResolutionRequiredException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
