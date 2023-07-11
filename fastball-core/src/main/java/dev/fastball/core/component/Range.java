package dev.fastball.core.component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.fastball.core.utils.RangeSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Getter
@Setter
@JsonSerialize(using = RangeSerialize.class)
public class Range<T extends Comparable<? super T>> {
    private List<T> data;

    public Range() {
        data = new ArrayList<>(2);
    }

    public Range(T start, T end) {
        this.data = Arrays.asList(start, end);
    }

    @JsonCreator
    public Range(List<T> data) {
        this.data = data;
    }

    public T getStart() {
        return data != null && data.size() > 0 ? data.get(0) : null;
    }

    public T getEnd() {
        return data != null && data.size() > 1 ? data.get(1) : null;
    }
}
