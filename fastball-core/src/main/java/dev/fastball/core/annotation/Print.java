package dev.fastball.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2023/1/15
 */
@Documented
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Print {

    /**
     * 打印使用的组件
     *
     * @return 对应组件的引用配置
     */
    RefComponent value();
}
