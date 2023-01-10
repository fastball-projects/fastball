package dev.fastball.core.component;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2023/1/8
 */
public interface AsyncTreeLookupAction<T, P> extends LookupActionComponent {

    Collection<T> loadLookupItems(T parent, P param);
}
