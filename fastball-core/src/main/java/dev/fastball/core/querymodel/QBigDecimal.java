package dev.fastball.core.querymodel;

import java.math.BigDecimal;

/**
 * @author Xyf
 */
public class QBigDecimal extends QType<BigDecimal> {
    public QBigDecimal() {
        this.operator = Operator.EQ;
    }
}
