package dev.fastball.core.config;

import lombok.Data;

import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2023/1/4
 */
@Data
public class FastballConfig {

    private Map<String, Menu> menus;
}
