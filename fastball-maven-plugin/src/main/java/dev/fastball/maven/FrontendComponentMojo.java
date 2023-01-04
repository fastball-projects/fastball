package dev.fastball.maven;

import dev.fastball.generate.generator.PortalCodeGenerator;
import dev.fastball.generate.utils.NodeJsUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static dev.fastball.generate.Constants.GENERATED_PATH;

/**
 * @author gengrong
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.RUNTIME, threadSafe = true)
public class FrontendComponentMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() {
        ClassLoader projectClassLoader = getClassLoader();
        File generatedCodeDir = new File(project.getBuild().getDirectory(), GENERATED_PATH);
        PortalCodeGenerator.generate(generatedCodeDir, projectClassLoader);
        try {
            NodeJsUtils.exec("pnpm i", generatedCodeDir);
            NodeJsUtils.exec("pnpm run build", generatedCodeDir);
            File staticResourceDir = new File(project.getBuild().getOutputDirectory(), "static");
            FileUtils.copyDirectory(new File(generatedCodeDir, "dist"), staticResourceDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
