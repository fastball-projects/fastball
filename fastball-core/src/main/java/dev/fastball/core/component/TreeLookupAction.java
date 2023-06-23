package dev.fastball.core.component;

import java.util.Collection;

/**
 * @author gr@fastball.dev
 * @since 2023/1/8
 */
public interface TreeLookupAction<T, S> extends LookupActionComponent {

    Collection<T> loadLookupItems(LookupActionParam<S> param);
}
