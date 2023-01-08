package dev.fastball.core.info.basic;

import dev.fastball.auto.value.annotation.AutoValue;

/**
 * @author gr@fastball.dev
 * @since 2022/12/30
 */
@AutoValue
public interface ValidationRuleInfo {
    Boolean required();

    Number len();

    Number min();

    Number max();

    String type();

    String pattern();

    String message();
}
