package dev.fastball.core.component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.core.utils.RangeSerialize;
import lombok.Getter;
import lombok.Setter;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Getter
@Setter
@JsonSerialize(using = RangeSerialize.class)
public class Range<T> {
    private T[] data;

    public Range() {
    }

    public Range(T start, T end) {
        this.data = (T[]) new Object[]{start, end};
    }

    @JsonCreator
    public Range(T[] data) {
        this.data = data;
    }

    public T getStart() {
        return data != null && data.length > 0 ? data[0] : null;
    }

    public T getEnd() {
        return data != null && data.length > 1 ? data[1] : null;
    }
}
