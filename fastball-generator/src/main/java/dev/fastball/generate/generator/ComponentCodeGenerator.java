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

import static dev.fastball.generate.Constants.*;

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
            File generateCodeFile = new File(componentDir, componentInfo.className().replaceAll("\\.", "/") + COMPONENT_SUFFIX);
            generateCodeToFile(componentInfo, generateCodeFile);
        }
        File mapperCodeFile = new File(componentDir, COMPONENT_MAPPER_FILE_NAME);
        generateMapper(componentInfoList, mapperCodeFile);
    }

    private static <T extends ComponentProps> void generateMapper(List<ComponentInfo<?>> componentInfoList, File codeFile) {
        StringBuilder content = new StringBuilder();
        StringBuilder mapperContent = new StringBuilder();
        for (int i = 0; i < componentInfoList.size(); i++) {
            ComponentInfo<?> componentInfo = componentInfoList.get(i);
            String componentPath = componentInfo.className().replaceAll("\\.", "/");
            String componentName = "Component" + i;
            content.append("import ").append(componentName);
            content.append(" from '@/").append(componentPath).append("';\n");

            mapperContent.append('"');
            mapperContent.append(componentInfo.className());
            mapperContent.append('"').append(':').append(componentName).append(',');
        }
        try {
            content.append("const ComponentMapper = {").append(mapperContent).append("};\n\n");
            content.append("export default ComponentMapper;");
            FileUtils.write(codeFile, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GenerateException(e);
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
            componentContent.append("let _f_b_props: Record<string, any> | null = null;\n")
                    .append("const buildProps = () => {\n")
                    .append("  _f_b_props = ").append(JsonUtils.toComponentJson(props)).append("}\n\n");

            componentContent.append("const Component = (props: any) => {\n")
                    .append("  if (_f_b_props === null) buildProps()\n")
                    .append("  return <OriginalComponent {..._f_b_props} {...props} />\n}\n\n");
//            componentContent.append("const Component = (props: any) => <OriginalComponent {..._f_b_props} {...props} />\n\n");
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
            importComponents.append("import ");
            if (ref.isDefaultComponent()) {
                importComponents.append(ref.getComponent());
            } else {
                importComponents.append("{ ").append(ref.getComponent()).append(" }");
            }
            importComponents.append(" from '")
                    .append(ref.getComponentPackage());
            if (ref.getComponentPath() != null && !ref.getComponentPath().isEmpty()) {
                importComponents.append("/").append(ref.getComponentPath());
            }
            importComponents.append("';\n");
        }
        return importComponents.toString();
    }

}
