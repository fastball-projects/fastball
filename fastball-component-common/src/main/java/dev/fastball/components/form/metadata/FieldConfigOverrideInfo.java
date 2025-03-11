package dev.fastball.components.form.metadata;

import dev.fastball.components.form.config.FormFieldConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldConfigOverrideInfo {

    private FormFieldConfig config;
    private Map<String, FieldConfigOverrideInfo> subFieldsConfigMap;

}
