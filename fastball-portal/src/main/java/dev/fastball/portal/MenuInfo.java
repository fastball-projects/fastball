package dev.fastball.portal;

import lombok.Data;

import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/31
 */
@Data
public class MenuInfo {
    private String title;
    private String component;
    private Map<String, MenuInfo> menus;
}
