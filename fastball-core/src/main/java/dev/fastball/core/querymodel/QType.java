package dev.fastball.core.querymodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xyf
 */
@Getter
@Setter
public class QType<T> {
    protected List<T> values;
    protected Operator operator;

    @JsonIgnore
    public T getValue() {
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }
        return values.get(0);
    }

    @JsonIgnore
    public void setValue(T value) {
        values = new ArrayList<>();
        values.add(value);
    }
}
