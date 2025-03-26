package dev.fastball.meta.basic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.fastball.auto.value.annotation.AutoValue;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@AutoValue
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "___class")
public interface AutoCompleteInfo {
    String packageName();

    AutoCompleteInputType inputType();

    String autoCompleteKey();

    String valueField();

    String[] dependencyFields();

    List<DisplayFieldInfo> fields();
}