package dev.fastball.core.component;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Getter
@Setter
public class DataResult<T> {

    private Long total;
    private Collection<T> data;

    public static <T> DataResult<T> build(Collection<T> data) {
        return build(null, data);
    }

    public static <T> DataResult<T> build(Long total, Collection<T> data) {
        return new DataResult<>(total, data);
    }

    public DataResult() {
    }

    public DataResult(Collection<T> data) {
        this(null, data);
    }

    public DataResult(Long total, Collection<T> data) {
        this.total = total;
        this.data = data;
    }
}
