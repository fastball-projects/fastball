package dev.fastball.generate.generator;

import dev.fastball.core.config.FastballConfig;
import dev.fastball.core.config.Menu;
import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.generate.Constants;
import dev.fastball.generate.exception.GenerateException;
import dev.fastball.generate.model.Route;
import dev.fastball.generate.utils.GeneratorUtils;
import dev.fastball.generate.utils.ResourceUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static dev.fastball.generate.Constants.ROUTES_PATH;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class PortalCodeGenerator {

    private static final String[] NEED_COPY_RESOURCES = new String[]{
            "tsconfig.json", "vite.config.ts", "index.html", "public/logo.svg", "types/index.d.ts", "src/main.tsx", "src/login.tsx", "src/login.scss", "src/route-builder.tsx"
    };

    public void generate(File generatedCodeDir, ClassLoader classLoader) {
        FastballConfig fastballConfig = ResourceUtils.loadFastballConfig(classLoader);
        List<ComponentInfo<?>> componentInfoList = ResourceUtils.loadComponentInfoList(classLoader);
        copyProjectFiles(generatedCodeDir);
        PackageJsonGenerator.generate(generatedCodeDir, componentInfoList, Constants.Portal.PACKAGE_FILE_SOURCE_PATH, fastballConfig);
        ComponentCodeGenerator.generate(generatedCodeDir, componentInfoList);
        generateRoutes(generatedCodeDir, componentInfoList, fastballConfig);
    }

    private void copyProjectFiles(File generatedCodeDir) {
        for (String needCopyResource : NEED_COPY_RESOURCES) {
            GeneratorUtils.copyResourceFile(Constants.Portal.SOURCE_PATH + needCopyResource, new File(generatedCodeDir, needCopyResource));
        }
    }

    // use template engine?
    private void generateRoutes(File generatedCodeDir, List<ComponentInfo<?>> componentInfoList, FastballConfig fastballConfig) {
        if (fastballConfig == null || fastballConfig.getMenus() == null || fastballConfig.getMenus().isEmpty()) {
            return;
        }
        File routesFile = new File(generatedCodeDir, ROUTES_PATH);
        List<Route> routes = new ArrayList<>();
        Map<String, ComponentInfo<?>> componentInfoMap = new HashMap<>();
        Set<ComponentInfo<?>> usedComponent = new HashSet<>();
        for (ComponentInfo<?> componentInfo : componentInfoList) {
            componentInfoMap.put(componentInfo.className(), componentInfo);
        }
        StringBuilder routesCode = new StringBuilder("import { MenuItemRoute } from '../types';\n\n");
        fastballConfig.getMenus().forEach((menuKey, menu) -> routes.add(buildMenu("/" + menuKey, menu, componentInfoMap, usedComponent)));
        usedComponent.forEach(componentInfo -> {
            routesCode.append("import ");
            routesCode.append(componentInfo.componentKey());
            routesCode.append(" from '@/");
            routesCode.append(componentInfo.componentPath());
            routesCode.append("';\n\n");
        });
        try {
            routesCode.append("const routes: MenuItemRoute[] = ");
            routesCode.append(JsonUtils.toComponentJson(routes));
            routesCode.append("\n\n");
            routesCode.append("export default routes;");
            FileUtils.write(routesFile, routesCode.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GenerateException(e);
        }
    }


    protected Route buildMenu(String menuPath, Menu menu, Map<String, ComponentInfo<?>> componentInfoMap, Set<ComponentInfo<?>> usedComponent) {
        Route.RouteBuilder routeBuilder = Route.builder().path(menuPath).name(menu.getTitle()).params(menu.getParams());
        if (menu.getComponent() != null) {
            ComponentInfo<?> componentInfo = componentInfoMap.get(menu.getComponent());
            if (componentInfo != null) {
                routeBuilder.component(componentInfo.componentKey());
                usedComponent.add(componentInfo);
            }
        } else if (menu.getMenus() != null) {
            List<Route> subRoutes = menu.getMenus().entrySet().stream()
                    .map(subMenuEntry -> buildMenu(menuPath + "/" + subMenuEntry.getKey(), subMenuEntry.getValue(), componentInfoMap, usedComponent))
                    .collect(Collectors.toList());
            routeBuilder.routes(subRoutes);
        }
        return routeBuilder.build();
    }
}
