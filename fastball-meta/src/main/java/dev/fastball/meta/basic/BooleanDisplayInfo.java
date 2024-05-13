package dev.fastball.meta.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gr@fastball.dev
 * @since 2023/1/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BooleanDisplayInfo {
    private String checkedChildren;
    private String unCheckedChildren;
}
