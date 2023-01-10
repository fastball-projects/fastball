package dev.fastball.core.info.basic;

import dev.fastball.auto.value.annotation.AutoValue;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@AutoValue
public interface TreeLookupInfo extends LookupInfo {
    boolean treeLookup = true;

    String childrenField();
}