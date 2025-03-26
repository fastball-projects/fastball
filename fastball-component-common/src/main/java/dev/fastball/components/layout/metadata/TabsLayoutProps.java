package dev.fastball.components.layout.metadata;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.meta.component.ComponentProps;

import java.util.List;

@AutoValue
@JsonDeserialize
public interface TabsLayoutProps extends ComponentProps {
    List<TabItemProps_AutoValue> items();

    int defaultActiveTab();

    boolean keepAlive();
}