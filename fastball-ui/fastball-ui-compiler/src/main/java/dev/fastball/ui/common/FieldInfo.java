package dev.fastball.ui.common;

import lombok.Data;

import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
public class FieldInfo {
    private String title;

    private String dataIndex;

    private String tooltip;

    private String valueType;

    private Map<String, Object> valueEnum;

    private boolean display;

    private Object fieldProps;

    private Map<String, Integer> colProps;

    private LookupActionInfo lookupAction;
}
