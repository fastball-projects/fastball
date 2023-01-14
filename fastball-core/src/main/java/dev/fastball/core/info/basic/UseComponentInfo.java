package dev.fastball.core.info.basic;

import dev.fastball.core.info.component.ReferencedComponentInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gr@fastball.dev
 * @since 2023/1/10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UseComponentInfo {

    private ReferencedComponentInfo componentInfo;

    private String valueKey;

    private String recordKey;
}
