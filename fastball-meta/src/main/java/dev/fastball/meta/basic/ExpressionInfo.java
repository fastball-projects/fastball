package dev.fastball.meta.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpressionInfo {
    private String[] fields;

    private ExpressionType type;

    private String expression;
}
