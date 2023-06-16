package dev.fastball.core.info.basic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.fastball.auto.value.annotation.AutoValue;

import java.util.List;
import java.util.Map;

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

    List<DependencyParamInfo> dependencyParams();

    List<LookupFillFieldInfo> extraFillFields();

    boolean multiple();

    boolean showSearch();
}