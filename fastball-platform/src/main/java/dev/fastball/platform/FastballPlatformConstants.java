package dev.fastball.platform;

/**
 * @author Geng Rong
 */
public interface FastballPlatformConstants {

    String PLATFORM_CONFIG_PATH_PREFIX = "classpath:/fastball-platform-";
    String PLATFORM_CONFIG_SUFFIX = ".yml";

    interface BusinessContext {
        String BUSINESS_CONTEXT_KEY_HEADER = "X-Business-Context-Key";
        String BUSINESS_CONTEXT_ID_HEADER = "X-Business-Context-Id";
    }
}
