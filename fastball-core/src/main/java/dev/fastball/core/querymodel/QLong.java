package dev.fastball.core.querymodel;

/**
 * @author Xyf
 */
public class QLong extends QType<Long> {
    public QLong() {
        this.operator = Operator.EQ;
    }
}
