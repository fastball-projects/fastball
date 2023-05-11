package dev.fastball.core.info.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupFillFieldInfo {
    private String fromField;
    private String targetField;
    private boolean onlyEmpty;
}
