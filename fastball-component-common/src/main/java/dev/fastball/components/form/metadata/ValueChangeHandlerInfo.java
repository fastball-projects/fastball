package dev.fastball.components.form.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValueChangeHandlerInfo {

    private String[] watchFields;

    private String handlerKey;
}
