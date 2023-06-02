package dev.fastball.core.info.basic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class FieldInfo {
    private String title;

    private List<String> dataIndex;

    private String tooltip;

    private String valueType;

    private String fieldType;

    private Map<String, EnumItem> valueEnum;

    private DisplayType display;

    private boolean readonly;

    private Object fieldProps;

    private Object formItemProps;

    private boolean entireRow;

    private LookupInfo lookup;

    private PopupInfo popupInfo;

    private Object defaultValue;

    private RefComponentInfo editModeComponent;

    private RefComponentInfo displayModeComponent;

    private ExpressionInfo expression;

    private List<ValidationRuleInfo> validationRules;

    private List<? extends FieldInfo> subFields;

    public FieldInfo(String dataIndex) {
        this.dataIndex = Collections.singletonList(dataIndex);
    }

    public void dataIndex(String dataIndex) {
        this.dataIndex = Collections.singletonList(dataIndex);
    }
}
