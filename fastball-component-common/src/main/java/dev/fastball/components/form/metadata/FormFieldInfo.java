package dev.fastball.components.form.metadata;

import dev.fastball.components.form.config.ConditionComposeType;
import dev.fastball.components.form.config.FieldDependencyType;
import dev.fastball.meta.basic.FieldInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class FormFieldInfo extends FieldInfo {
    private List<FieldDependencyInfo> fieldDependencyInfoList;

    private ConditionComposeType conditionComposeType;

    private FieldDependencyType fieldDependencyType;

    private String addonBefore;

    private String addonAfter;

    private String placeholder;
}
