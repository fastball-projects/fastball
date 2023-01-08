package dev.fastball.generate.generator;

import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.info.component.ComponentProps;
import dev.fastball.core.info.component.ReferencedComponentInfo;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.generate.exception.GenerateException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static dev.fastball.generate.Constants.COMPONENT_PATH;
import static dev.fastball.generate.Constants.COMPONENT_SUFFIX;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class ComponentCodeGenerator {

    private ComponentCodeGenerator() {
    }

    public static void generate(File generatedCodeDir, List<ComponentInfo<?>> componentInfoList) {
        File componentDir = new File(generatedCodeDir, COMPONENT_PATH);
        for (ComponentInfo<?> componentInfo : componentInfoList) {
            File generateCodeFile = new File(componentDir, componentInfo.componentKey() + COMPONENT_SUFFIX);
            generateCodeToFile(componentInfo, generateCodeFile);
        }
    }

    private static <T extends ComponentProps> void generateCodeToFile(ComponentInfo<T> componentInfo, File codeFile) {
        T props = componentInfo.props();
        try {
            StringBuilder componentContent = new StringBuilder();
            componentContent.append("import { ").append(componentInfo.componentName())
                    .append(" as OriginalComponent } from '")
                    .append(componentInfo.material().getNpmPackage()).append("';\n\n");
            componentContent.append(generateImportComponent(props));
            componentContent.append("const _f_b_props = ").append(JsonUtils.toComponentJson(props)).append("\n\n");
            componentContent.append("const Component = (props: any) => <OriginalComponent {..._f_b_props} {...props} />\n\n");
            componentContent.append("export default Component;");
            FileUtils.write(codeFile, componentContent, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GenerateException(e);
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

}
