package dev.fastball.core.component;

import dev.fastball.core.annotation.Field;
import dev.fastball.core.annotation.SimpleQueryable;
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
@NoArgsConstructor
@AllArgsConstructor
public class DefaultDataRecord implements DataRecord {
    @Field(display = DisplayType.Disabled)
    @SimpleQueryable.Ignore
    private Map<String, Boolean> recordActionAvailableFlags;
}
