package dev.fastball.core.component;

import dev.fastball.core.annotation.Field;
import dev.fastball.core.info.basic.DisplayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DataRecord {
    @Field(display = DisplayType.Disabled)
    private Map<String, Boolean> recordActionAvailableFlags;
}
