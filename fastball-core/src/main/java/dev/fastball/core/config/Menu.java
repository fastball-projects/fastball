package dev.fastball.core.config;

import lombok.Data;

import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/31
 */
@Data
public class Menu {
    private String title;
    private String component;
    private Object params;
    private Map<String, Menu> menus;
}
