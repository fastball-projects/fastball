package dev.fastball.components.common.metadata.sort;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author gr@fastball.dev
 * @since 2023/1/10
 */
public enum SortOrder {
    Descend("descend"),
    Ascend("ascend");

    private String value;

    SortOrder(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
