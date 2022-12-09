package dev.fastball.ui;

import com.fasterxml.jackson.annotation.JsonGetter;

public interface LookupItem<T> {

    @JsonGetter("label")
    String getLabel();

    @JsonGetter("value")
    T getValue();
}
