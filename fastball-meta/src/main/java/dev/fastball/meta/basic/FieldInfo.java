package dev.fastball.meta.basic;

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
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "___class")
public class FieldInfo implements Comparable<FieldInfo> {
    private String title;

    private List<String> dataIndex;

    private String tooltip;

    private String placeholder;

    private String valueType;

    private String fieldType;

    private Map<String, EnumItem> valueEnum;

    private DisplayType display;

    private boolean readonly;

    private Object fieldProps;

    private Object formItemProps;

    private boolean entireRow;

    private LookupInfo lookup;

    private AutoCompleteInfo autoComplete;

    private PopupInfo popupInfo;

    private Object defaultValue;

    private int order;

    private Integer digitPrecision;

    private DateFieldDefaultValueInfo dateDefaultValue;

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

    @Override
    public int compareTo(FieldInfo target) {
        return Integer.compare(this.getOrder(), target == null ? 0 : target.getOrder());
    }
}
