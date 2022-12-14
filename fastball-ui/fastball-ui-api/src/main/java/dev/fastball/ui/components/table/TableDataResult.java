package dev.fastball.ui.components.table;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2022/12/9
 */
@Data
@Builder
public class TableDataResult<T> {

    private Integer total;
    private Collection<T> data;
}
