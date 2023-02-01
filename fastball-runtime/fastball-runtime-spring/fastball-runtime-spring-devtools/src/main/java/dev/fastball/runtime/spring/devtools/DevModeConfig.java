package dev.fastball.runtime.spring.devtools;

import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2023/1/28
 */
@Data
public class DevModeConfig {
    private String editorHost = "localhost";
    private String editorPort = "23333";
    private String proxyTarget = "http://localhost:8080";
}
