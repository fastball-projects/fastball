package dev.fastball.core.info.basic;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.core.annotation.AutoComplete;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2023/01/08
 */
@AutoValue
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public interface AutoCompleteInfo {
    String packageName();

    AutoComplete.InputType inputType();

    String autoCompleteKey();

    String valueField();

    String[] dependencyFields();

    List<DisplayFieldInfo> fields();
}