package dev.fastball.generate;

import static dev.fastball.core.Constants.FASTBALL_RESOURCE_PREFIX;
import static dev.fastball.core.Constants.FASTBALL_VIEW_SUFFIX;

public interface Constants {

    interface Defaults {
        String LOGO_PATH = "/logo.svg";
        String DEV_SERVER_URL = "http://localhost:8080";
        String TITLE = "Fastball";
        String DESC = "一款面向 Java 开发人员的界面开发框架";
        String COPYRIGHT = "©2023 杭州范数科技有限公司";
    }

    interface Portal {

        String SOURCE_PATH = "/portal/";

        String PACKAGE_FILE_SOURCE_PATH = SOURCE_PATH + "package.json";

    }

    String GENERATED_PATH = "generated-fastball";
    String COMPONENT_PATH = "src/";
    String ROUTES_PATH = "src/routes.tsx";
    String PACKAGE_FILE_NAME = "package.json";

    String CONFIG_FILE_NAME = "config.json";

    String ASSETS_FILE_NAME = "assets.json";
    String COMPONENT_SUFFIX = ".tsx";

    String VIEW_FILE_PATH = "classpath*:/" + FASTBALL_RESOURCE_PREFIX + "**/*" + FASTBALL_VIEW_SUFFIX;
}
