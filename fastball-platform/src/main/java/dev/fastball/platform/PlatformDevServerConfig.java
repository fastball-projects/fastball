package dev.fastball.platform;

import lombok.Data;

@Data
public class PlatformDevServerConfig {

    /**
     * 监听的地址, 一般默认为 localhost
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 是否自动打开浏览器
     */
    private Boolean open;
}
