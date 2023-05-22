package dev.fastball.core.querymodel;

/**
 * @author Xyf
 */
public class QString extends QType<String> {
    public QString() {
        this.operator = Operator.LIKE;
    }
}
