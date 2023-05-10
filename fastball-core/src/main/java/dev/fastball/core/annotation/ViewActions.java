package dev.fastball.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewActions {

    /**
     * @return 组件级别的操作
     * @deprecated use {@link ViewActions#actions()}
     */
    @Deprecated
    ViewAction[] value() default {};

    ViewAction[] actions() default {};

    ViewAction[] recordActions() default {};

    /**
     * 是否覆盖已有 ViewAction.
     * 如为 True, 则会忽略父类上的 ViewActions 声明.
     * 如为 False, 则会在父类的 ViewActions 声明之后, 增加额外的 ViewAction
     *
     * @return 是否覆盖已有 ViewAction
     */
    boolean override() default false;
}
