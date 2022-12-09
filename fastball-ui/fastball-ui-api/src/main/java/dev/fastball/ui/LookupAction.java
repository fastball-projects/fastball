package dev.fastball.ui;

import java.util.Collection;

public interface LookupAction<T extends LookupItem<?>, P> {

    Collection<T> loadLookupItems(P param);
}
