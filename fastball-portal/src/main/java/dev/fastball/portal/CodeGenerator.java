package dev.fastball.portal;

import dev.fastball.core.utils.JsonUtils;
import dev.fastball.core.material.MaterialRegistry;
import dev.fastball.core.PackageModel;
import dev.fastball.core.component.ComponentProps;
import dev.fastball.core.component.ReferencedComponentInfo;
import dev.fastball.core.component.ComponentInfo;
import dev.fastball.core.material.UIMaterial;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static dev.fastball.portal.Constants.*;


public class CodeGenerator {

    private CodeGenerator() {
    }


    public static void generate(File generatedCodeDir, MaterialRegistry materialRegistry, List<ComponentInfo<?>> componentInfoList, Map<String, MenuInfo> menus) {
        copyProjectFiles(generatedCodeDir);
        generateComponents(generatedCodeDir, componentInfoList);
        generateRoutes(generatedCodeDir, componentInfoList, menus);
        generatePackageJson(generatedCodeDir, materialRegistry.getMaterials());
        try {
            Runtime.getRuntime().exec("pnpm i", null, generatedCodeDir).waitFor();
            new Thread(() -> {
                Process devProcess;
                try {
                    devProcess = Runtime.getRuntime().exec("pnpm run dev", null, generatedCodeDir);
                    IOUtils.copy(devProcess.getInputStream(), System.out);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
//            Runtime.getRuntime().exec("pnpm run build", null, generatedCodeDir).waitFor();
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

    private static void generateRoutes(File generatedCodeDir, List<ComponentInfo<?>> componentInfoList, Map<String, MenuInfo> menus) {
        File routesFile = new File(generatedCodeDir, ROUTES_PATH);
        List<Route> routes = new ArrayList<>();
        Map<String, ComponentInfo<?>> componentInfoMap = new HashMap<>();
        Set<ComponentInfo<?>> usedComponent = new HashSet<>();
        for (ComponentInfo<?> componentInfo : componentInfoList) {
            componentInfoMap.put(componentInfo.className(), componentInfo);
        }
        StringBuilder routesCode = new StringBuilder();
        for (Map.Entry<String, MenuInfo> menu : menus.entrySet()) {
            routes.add(buildMenu("/" + menu.getKey(), menu.getValue(), componentInfoMap, usedComponent));
        }
        for (ComponentInfo<?> componentInfo : usedComponent) {
            routesCode.append("import ").append(componentInfo.componentKey())
                    .append(" from '@/components/").append(componentInfo.componentKey()).append("';\n");
        }
        try {
            routesCode.append("const routes = ");
            routesCode.append(JsonUtils.toPrettyJson(routes));
            routesCode.append("\n");
            routesCode.append("\n");
            routesCode.append("export default routes;");
            FileUtils.write(routesFile, routesCode.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Route buildMenu(String menuPath, MenuInfo menuInfo, Map<String, ComponentInfo<?>> componentInfoMap, Set<ComponentInfo<?>> usedComponent) {
        Route.RouteBuilder routeBuilder = Route.builder().path(menuPath).name(menuInfo.getTitle());
        if (StringUtils.hasText(menuInfo.getComponent())) {
            ComponentInfo<?> componentInfo = componentInfoMap.get(menuInfo.getComponent());
            if (componentInfo != null) {
                routeBuilder.component(componentInfo.componentKey());
                usedComponent.add(componentInfo);
            }
        } else if (menuInfo.getMenus() != null) {
            List<Route> subRoutes = menuInfo.getMenus().entrySet().stream()
                    .map(subMenuEntry -> buildMenu(menuPath + "/" + subMenuEntry.getKey(), subMenuEntry.getValue(), componentInfoMap, usedComponent))
                    .collect(Collectors.toList());
            routeBuilder.routes(subRoutes);
        }
        return routeBuilder.build();

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
