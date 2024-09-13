package dev.fastball.generate.generator;

import dev.fastball.core.config.FastballConfig;
import dev.fastball.core.config.Menu;
import dev.fastball.meta.component.ComponentInfo;
import dev.fastball.meta.utils.JsonUtils;
import dev.fastball.generate.Constants;
import dev.fastball.generate.exception.GenerateException;
import dev.fastball.generate.model.FastballFrontendConfig;
import dev.fastball.generate.model.Route;
import dev.fastball.generate.utils.GeneratorUtils;
import dev.fastball.generate.utils.ResourceUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static dev.fastball.generate.Constants.*;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class PortalCodeGenerator {

    private static final String[] NEED_COPY_RESOURCES = new String[]{
            "tsconfig.json", "vite.config.ts", "index.html", "public/logo.svg", "types/index.d.ts", "src/main.tsx", "src/change-password.tsx", "src/login.tsx", "src/utils.ts", "src/login.scss", "src/message.tsx", "src/route-builder.tsx"
    };

    public void generate(File generatedCodeDir, ClassLoader classLoader) {
        FastballConfig fastballConfig = ResourceUtils.loadFastballConfig(classLoader);
        List<ComponentInfo<?>> componentInfoList = ResourceUtils.loadComponentInfoList(classLoader);
        copyProjectFiles(generatedCodeDir);
        PackageJsonGenerator.generate(generatedCodeDir, componentInfoList, Constants.Portal.PACKAGE_FILE_SOURCE_PATH, fastballConfig);
        ComponentCodeGenerator.generate(generatedCodeDir, componentInfoList);
        generateRoutes(generatedCodeDir, componentInfoList, fastballConfig);
        generateConfig(generatedCodeDir, fastballConfig);
    }

    private void generateConfig(File generatedCodeDir, FastballConfig fastballConfig) {
        File packageJsonFile = new File(generatedCodeDir, CONFIG_FILE_NAME);
        FastballFrontendConfig config = new FastballFrontendConfig();
        String devServerUrl = fastballConfig.getDevServerUrl();
        if (!StringUtils.hasText(devServerUrl)) {
            devServerUrl = Defaults.DEV_SERVER_URL;
        }
        Map<String, String> devServerProxy = new HashMap<>();
        devServerProxy.put("/api", devServerUrl);
        devServerProxy.put("/favicon.ico", devServerUrl);
        if (StringUtils.hasText(fastballConfig.getLogo())) {
            devServerProxy.put(fastballConfig.getLogo(), devServerUrl);
            config.setLogo(fastballConfig.getLogo());
        } else {
            config.setLogo(Defaults.LOGO_PATH);
        }
        config.setDevServerProxy(devServerProxy);
        config.setEnableNotice(fastballConfig.isEnableNotice());
        if (StringUtils.hasText(fastballConfig.getTitle())) {
            config.setTitle(fastballConfig.getTitle());
        } else {
            config.setTitle(Defaults.TITLE);
        }
        if (StringUtils.hasText(fastballConfig.getDescription())) {
            config.setDescription(fastballConfig.getDescription());
        }
        if (StringUtils.hasText(fastballConfig.getCopyright())) {
            config.setCopyright(fastballConfig.getCopyright());
        } else {
            config.setCopyright(Defaults.COPYRIGHT);
        }
        try {
            FileUtils.write(packageJsonFile, JsonUtils.toPrettyJson(config), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new GenerateException(e);
        }
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
