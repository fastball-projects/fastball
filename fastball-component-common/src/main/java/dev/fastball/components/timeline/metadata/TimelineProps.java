package dev.fastball.components.timeline.metadata;

import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.meta.action.ActionInfo;
import dev.fastball.meta.component.ComponentProps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author gr@fastball.dev
 * @since 2023/1/9
 */
@AutoValue
public interface TimelineProps extends ComponentProps {

    TimelineFieldNames fieldNames();

    List<ActionInfo> recordActions();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    final class TimelineFieldNames {
        public static final TimelineFieldNames DEFAULT = new TimelineFieldNames("id", "left", "right", "color");

        private String key;
        private String left;
        private String right;
        private String color;
    }
}
