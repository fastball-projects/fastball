package dev.fastball.core.info.basic;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 弹出的方向
 *
 * @author gr@fastball.dev
 * @since 2023/01/02
 */
public enum PlacementType {
    /**
     * 从上方
     */
    Top("top"),
    /**
     * 从下方
     */
    Bottom("bottom"),
    /**
     * 从右侧
     */
    Right("right"),
    /**
     * 从左侧
     */
    Left("left");

    @JsonValue
    private String value;

    PlacementType(String value) {
        this.value = value;
    }
}
