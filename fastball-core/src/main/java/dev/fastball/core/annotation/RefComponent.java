package dev.fastball.core.annotation;

import dev.fastball.core.component.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2023/1/28
 */
@Documented
@Target({})
public @interface RefComponent {

    /**
     * 引用的组件 Class
     *
     * @return 对应组件的 Class
     */
    Class<? extends Component> value();

    /**
     * 是否仅传入当前字段的值, 某些组件希望仅传入当前字段, 可以开启此属性
     * 此配置可以理解为是简化配置的糖, 行为逻辑上与 dataPath 配置字段路径一致
     *
     * @return 是否仅传入当前字段
     */
    boolean currentFieldInput() default false;

    /**
     * 调用 component 时传入的参数路径, 只有 @RefComponent.currentFieldInput = false 时才生效
     * 默认传递给组件的是当前记录, 如果需要传递某个字段的值, 可以通过 dataPath 来指定.
     * 比如某数据 `{ user: { name: "abc" }}`, 如果我们希望组件接收的内容是 .user.name, 则可以设置 dataPath 为 ["user","name"]
     *
     * @return 传递给组件的参数路径
     */
    String[] dataPath() default {};

    /**
     * @return 当前记录值传给组件时的 props key, 默认是 input
     */
    String propsKey() default "input";
}
