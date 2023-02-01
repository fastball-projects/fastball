package dev.fastball.core.component;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
public interface RecordActionFilter<T> {
    boolean filter(T record);
}
