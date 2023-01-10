package dev.fastball.core.info.basic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.fastball.auto.value.annotation.AutoValue;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@AutoValue
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface LookupInfo {
    String packageName();

    String lookupKey();

    String labelField();

    String valueField();

    boolean multiple();
}