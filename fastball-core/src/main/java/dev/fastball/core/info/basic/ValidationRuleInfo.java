package dev.fastball.core.info.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRuleInfo {
    private Boolean required;

    private Number len;

    private Number min;

    private Number max;

    private String type;

    private String pattern;

    private String message;
}
