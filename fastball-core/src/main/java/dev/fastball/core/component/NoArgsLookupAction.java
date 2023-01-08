package dev.fastball.core.component;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2023/1/8
 */
public interface NoArgsLookupAction<T> extends LookupAction<T, Object> {

    Collection<T> loadLookupItems();

    default Collection<T> loadLookupItems(Object param) {
        return loadLookupItems();
    }
}
