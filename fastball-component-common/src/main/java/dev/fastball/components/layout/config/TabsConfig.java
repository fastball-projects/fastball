package dev.fastball.components.layout.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TabsConfig {

    /**
     * tab项
     *
     * @return tab项
     */
    TabItem[] items();

    /**
     * 默认选中的tab，默认为0
     *
     * @return 默认选中的tab
     */
    int defaultActiveTab() default 0;

    /**
     * 是否开启缓存，默认为false，开启后，切换tab不会重新加载组件
     *
     * @return 是否开启缓存
     */
    boolean keepAlive() default false;
}
