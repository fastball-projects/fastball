package dev.fastball.runtime.spring.devtools;

import dev.fastball.core.config.Menu;
import dev.fastball.core.info.component.ComponentInfo;
import dev.fastball.core.utils.JsonUtils;
import dev.fastball.generate.Constants;
import dev.fastball.generate.generator.PortalCodeGenerator;
import dev.fastball.generate.model.Route;
import dev.fastball.generate.utils.GeneratorUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
public class DevModePortalCodeGenerator extends PortalCodeGenerator {

    private static final String DEV_MODE_ROUTE_BUILDER_PATH = "src/dev-mode-route-builder.tsx";
    private static final String ROUTE_BUILDER_PATH = "src/route-builder.tsx";

    private static final String DEV_MODE_CONFIG_PATH = "dev-mode-config.json";

    @Override
    public void generate(File generatedCodeDir, ClassLoader classLoader) {
        super.generate(generatedCodeDir, classLoader);
        try {
            FileUtils.write(new File(generatedCodeDir, DEV_MODE_CONFIG_PATH), JsonUtils.toPrettyJson(new DevModeConfig()), StandardCharsets.UTF_8);
            GeneratorUtils.copyResourceFile(Constants.Portal.SOURCE_PATH + DEV_MODE_ROUTE_BUILDER_PATH, new File(generatedCodeDir, ROUTE_BUILDER_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Route buildMenu(String menuPath, Menu menu, Map<String, ComponentInfo<?>> componentInfoMap, Set<ComponentInfo<?>> usedComponent) {
        Route route = super.buildMenu(menuPath, menu, componentInfoMap, usedComponent);
        route.setComponentFullName(menu.getComponent());
        return route;
    }
}
