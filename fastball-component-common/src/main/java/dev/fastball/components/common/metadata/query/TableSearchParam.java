package dev.fastball.components.common.metadata.query;

import lombok.Getter;
import lombok.Setter;

/**
 * @author gr@fastball.dev
 * @since 2023/1/24
 */
@Getter
@Setter
public class TableSearchParam<S> extends SearchParam {
    private S search;
}
