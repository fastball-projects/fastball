package dev.fastball.maven;

import dev.fastball.generate.generator.PortalCodeGenerator;
import dev.fastball.generate.utils.ExecUtils;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static dev.fastball.generate.Constants.GENERATED_PATH;

/**
 * @author gengrong
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class FrontendComponentMojo extends AbstractMojo {

    private final PortalCodeGenerator portalCodeGenerator = new PortalCodeGenerator();

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;


    @Override
    public void execute() {
        ClassLoader projectClassLoader = getClassLoader();
        File generatedCodeDir = new File(project.getBuild().getDirectory(), GENERATED_PATH);
        portalCodeGenerator.generate(generatedCodeDir, projectClassLoader);
        try {
            ExecUtils.checkNodeAndPNPM();
            OutputStream infoOut = new MavenLogOutputStream(getLog(), MavenLogOutputStream.LogLevel.INFO);
            OutputStream errorOut = new MavenLogOutputStream(getLog(), MavenLogOutputStream.LogLevel.ERROR);
            ExecUtils.exec("pnpm i", generatedCodeDir, infoOut, errorOut);
            ExecUtils.exec("pnpm run build", generatedCodeDir, infoOut, errorOut);
            File staticResourceDir = new File(project.getBuild().getOutputDirectory(), "static");
            FileUtils.copyDirectory(new File(generatedCodeDir, "dist"), staticResourceDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassLoader getClassLoader() {
        try {
            List<String> classpathElements = project.getArtifacts().stream().map(artifact -> {
                File file = artifact.getFile();
                return file != null ? file.getPath() : null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            classpathElements.add(project.getBuild().getOutputDirectory());
            // 转为 URL 数组
            URL[] urls = new URL[classpathElements.size()];
            for (int i = 0; i < classpathElements.size(); ++i) {
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();
            }
            // 自定义类加载器
            return new URLClassLoader(urls, this.getClass().getClassLoader());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
