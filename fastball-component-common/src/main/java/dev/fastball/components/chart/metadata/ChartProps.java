package dev.fastball.components.chart.metadata;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.fastball.auto.value.annotation.AutoValue;
import dev.fastball.meta.component.ComponentProps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@AutoValue
public interface ChartProps extends ComponentProps {

    ChartType type();

    String title();

    ChartFieldNames fieldNames();

    String palette();

    boolean bordered();

    String borderColor();


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    final class ChartFieldNames {
        // xField 默认序列化会变成 xfield
        @JsonProperty("xField")
        private String xField;
        @JsonProperty("yField")
        private String yField;
        private String seriesField;
    }
}
