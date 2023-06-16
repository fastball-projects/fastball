package dev.fastball.core.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2023/5/10
 */
@Documented
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface DependencyParam {

    /**
     * @return 入参的 Key
     */
    String paramKey();

    /**
     * @return 入参的路径, rootValue 为 True 时, 以根对象出发, rootValue 为 False 时, 以当前对象出发
     */
    String[] paramPath();

    /**
     * 是否是对象, 如果为否则为当前层级对象, 为 True 则为跟对象
     */
    boolean rootValue() default false;
}
