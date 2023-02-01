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
public class RefComponentInfo {

    private ReferencedComponentInfo componentInfo;

    private boolean currentFieldInput;

    private String[] dataPath;

    private String propsKey;
}
