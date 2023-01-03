package dev.fastball.maven.generator;

import dev.fastball.core.PackageModel;
import dev.fastball.core.component.ComponentInfo;
import dev.fastball.core.material.UIMaterial;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.core.material.MaterialRegistry;
import dev.fastball.core.component.ComponentProps;
import dev.fastball.core.component.ReferencedComponentInfo;
import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static dev.fastball.maven.Constants.*;

public class CodeGenerator {

    private CodeGenerator() {
    }


    public static void generate(MavenProject project, MaterialRegistry materialRegistry, List<ComponentInfo<?>> componentInfoList) {
        File generatedCodeDir = new File(project.getBuild().getDirectory(), GENERATED_PATH);
        copyProjectFiles(generatedCodeDir);
        generateComponents(generatedCodeDir, componentInfoList);
        generatePackageJson(generatedCodeDir, materialRegistry.getMaterials());
        try {
            Runtime.getRuntime().exec("pnpm i", null, generatedCodeDir).waitFor();
            Runtime.getRuntime().exec("pnpm run build", null, generatedCodeDir).waitFor();
            FileUtils.copyDirectory(new File(generatedCodeDir, "dist"), new File(project.getBuild().getResources().get(0).getDirectory(), "static"));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends ComponentProps> void generateCodeToFile(ComponentInfo<T> componentInfo, File codeFile) {
        T props = componentInfo.props();
        try {
            String componentContent = "import { " + componentInfo.componentName() + " as OriginalComponent } from '" +
                    componentInfo.material().getNpmPackage() + "';\n\n";
            componentContent += generateImportComponent(props);
            componentContent += "const _f_b_props = " + JsonUtils.toComponentJson(props) + "\n\n" +
                    "const Component = (props: any) => <OriginalComponent {..._f_b_props} {...props} />\n\n" +
                    "export default Component;";
            FileUtils.write(codeFile, componentContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateImportComponent(ComponentProps props) {
        if (props.referencedComponentInfoList() == null) {
            return "";
        }
        StringBuilder importComponents = new StringBuilder();
        for (ReferencedComponentInfo ref : props.referencedComponentInfoList()) {
            importComponents.append("import ").append(ref.component()).append(" from '")
                    .append(ref.componentPackage()).append("/components/").append(ref.componentName()).append("';\n");
        }
        return importComponents.toString();
    }


    private static void generatePackageJson(File generatedCodeDir, Collection<UIMaterial> dependMaterials) {
        File packageJsonFile = new File(generatedCodeDir, PACKAGE_FILE_NAME);
        try {
            String packageJson = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
            PackageModel packageModel = JsonUtils.fromJson(packageJson, PackageModel.class);
            for (UIMaterial dependMaterial : dependMaterials) {
                packageModel.getDependencies().put(dependMaterial.getNpmPackage(), dependMaterial.getNpmVersion());
            }
            FileUtils.write(packageJsonFile, JsonUtils.toPrettyJson(packageModel), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void generateComponents(File generatedCodeDir, List<ComponentInfo<?>> componentInfoList) {
        File componentDir = new File(generatedCodeDir, COMPONENT_PATH);
        for (ComponentInfo<?> componentInfo : componentInfoList) {
            File generateCodeFile = new File(componentDir, componentInfo.componentKey() + COMPONENT_SUFFIX);
            generateCodeToFile(componentInfo, generateCodeFile);
        }
    }


    private static void copyProjectFiles(File generatedCodeDir) {
        try {
            FileUtils.copyInputStreamToFile(CodeGenerator.class.getResourceAsStream("/portal/package.json"), new File(generatedCodeDir, "package.json"));
            FileUtils.copyInputStreamToFile(CodeGenerator.class.getResourceAsStream("/portal/tsconfig.json"), new File(generatedCodeDir, "tsconfig.json"));
            FileUtils.copyInputStreamToFile(CodeGenerator.class.getResourceAsStream("/portal/vite.config.ts"), new File(generatedCodeDir, "vite.config.ts"));
            FileUtils.copyInputStreamToFile(CodeGenerator.class.getResourceAsStream("/portal/index.html"), new File(generatedCodeDir, "index.html"));
            FileUtils.copyInputStreamToFile(CodeGenerator.class.getResourceAsStream("/portal/src/main.tsx"), new File(generatedCodeDir, "src/main.tsx"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
