package dev.fastball.ui.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@Data
@Builder
public class ValidationRule {
    private Boolean required;
    private Number len;
    private Number min;
    private Number max;
    private String type;
    private String pattern;
    private String message;
}
