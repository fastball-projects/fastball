package dev.fastball.components.layout.config;

import dev.fastball.core.component.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface TabItem {

    /**
     * @return tab标签的标题
     */
    String label();

    /**
     * @return tab标签的组件
     */
    Class<? extends Component> component();

    /**
     * 组件的输入参数, 希望为特定 Tab 的组件传入参数时, 可以使用
     * 格式为 Json 的字符串, 如: {"key1": "value1", "key2": "value2"}
     * 组件侧使用 input 参数接收
     *
     * @return 组件的输入参数
     */
    String componentInput() default "";
}
