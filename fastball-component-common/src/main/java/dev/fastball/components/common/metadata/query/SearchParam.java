package dev.fastball.components.common.metadata.query;

import dev.fastball.components.common.metadata.sort.SortOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author Xyf
 */
@Getter
@Setter
@ToString
public class SearchParam {
    private String keyword;
    private Long pageSize;
    private Long current;
    private Map<String, SortOrder> sortFields;

    public boolean page() {
        return pageSize != null && current != null;
    }
}
