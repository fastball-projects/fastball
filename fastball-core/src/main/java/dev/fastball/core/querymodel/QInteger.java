package dev.fastball.core.querymodel;

/**
 * @author Xyf
 */
public class QInteger extends QType<Integer> {
    public QInteger() {
        this.operator = Operator.EQ;
    }
}
