package dev.fastball.components.chart.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AreaChartConfig {
    /**
     * @return 图表标题
     */
    String title();

    String xField();

    String yField();

    String seriesField() default "";

    /**
     * @return 调色板, 可以参考 <a href="https://ant-design-charts.antgroup.com/options/plots/palette">AntV Palette</a>
     */
    String palette() default "";

    /**
     * 是否显示边框, 如果开启, 会在每个扇形上显示边框, 默认为false
     * 边框颜色为borderColor, 默认为白色
     *
     * @return 是否显示边框
     */
    boolean bordered() default false;

    /**
     * @return 边框颜色, 默认为白色
     */
    String borderColor() default "#fff";
}
