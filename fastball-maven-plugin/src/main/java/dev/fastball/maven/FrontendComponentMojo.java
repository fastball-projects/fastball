package dev.fastball.maven;

import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.platform.core.FastballPlatform;
import dev.fastball.platform.core.FastballPlatformLoader;
import dev.fastball.platform.core.utils.ResourceUtils;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author gengrong
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, threadSafe = true)
public class FrontendComponentMojo extends AbstractMojo {


    static String GENERATED_PATH = "generated-fastball";

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;


    @Override
    public void execute() {
        ClassLoader classLoader = getClassLoader();
        File workspaceDir = new File(project.getBuild().getDirectory(), GENERATED_PATH);

        Map<String, List<ComponentInfo<?>>> componentPlatformGroup = ResourceUtils.loadComponentInfoMap(classLoader);
        OutputStream infoOut = new MavenLogOutputStream(getLog(), MavenLogOutputStream.LogLevel.INFO);
        OutputStream errorOut = new MavenLogOutputStream(getLog(), MavenLogOutputStream.LogLevel.ERROR);
        File targetDir = new File(project.getBuild().getOutputDirectory(), "static");
        for (FastballPlatform<?> fastballPlatform : FastballPlatformLoader.getAllPlatformPortal(classLoader)) {
            File platformWorkspaceDir = new File(workspaceDir, fastballPlatform.platform());
            File platformTargetDir = new File(targetDir, fastballPlatform.platform());
            fastballPlatform.build(platformWorkspaceDir, platformTargetDir, componentPlatformGroup.get(fastballPlatform.platform()), infoOut, errorOut);
        }

//        portalCodeGenerator.generate(generatedCodeDir, projectClassLoader);
//            ExecUtils.checkNodeAndPNPM();
//            OutputStream infoOut = new MavenLogOutputStream(getLog(), MavenLogOutputStream.LogLevel.INFO);
//            OutputStream errorOut = new MavenLogOutputStream(getLog(), MavenLogOutputStream.LogLevel.ERROR);
//            ExecUtils.exec("pnpm i", generatedCodeDir, infoOut, errorOut);
//            ExecUtils.exec("pnpm run build", generatedCodeDir, infoOut, errorOut);
//            File staticResourceDir = new File(project.getBuild().getOutputDirectory(), "static");
//            FileUtils.copyDirectory(new File(generatedCodeDir, "dist"), staticResourceDir);
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
