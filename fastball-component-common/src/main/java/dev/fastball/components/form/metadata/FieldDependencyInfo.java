package dev.fastball.components.form.metadata;

import dev.fastball.components.form.config.FieldDependencyCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldDependencyInfo {

    private String field;

    private String value;

    private FieldDependencyCondition condition;
}
