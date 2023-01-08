package dev.fastball.core.info.basic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.fastball.auto.value.annotation.AutoValue;

import java.util.List;
import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@AutoValue
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface FieldInfo {
    String title();

    void title(String title);

    String dataIndex();

    void dataIndex(String dataIndex);

    String tooltip();

    void tooltip(String tooltip);

    String valueType();

    void valueType(String valueType);

    Map<String, Object> valueEnum();

    void valueEnum(Map<String, Object> valueEnum);

    boolean display();

    void display(boolean display);

    Object fieldProps();

    void fieldProps(Object fieldProps);

    Map<String, Integer> colProps();

    void colProps(Map<String, Integer> colProps);

    LookupInfo lookupAction();

    void lookupAction(LookupInfo lookupAction);

    List<ValidationRuleInfo> validationRules();

    void validationRules(List<ValidationRuleInfo> validationRules);
}
