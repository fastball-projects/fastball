package dev.fastball.components.timeline.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2023/1/8
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimelineConfig {

    /**
     * @return 数据表示字段, 默认为 id
     */
    String keyField() default "id";

    /**
     * @return 时间轴的左侧字段
     */
    String leftField();

    /**
     * @return 时间轴的右侧字段
     */
    String rightField();

    String colorField() default "";
}
