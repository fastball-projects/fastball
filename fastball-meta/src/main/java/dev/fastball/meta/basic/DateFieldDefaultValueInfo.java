package dev.fastball.meta.basic;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Geng Rong
 */
@Data
@NoArgsConstructor
public class DateFieldDefaultValueInfo {
    private DateDefaultValue defaultValue;
    private long offset;
    private DateOffsetUnit offsetUnit;
}
