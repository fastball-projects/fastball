package dev.fastball.core.querymodel;

import java.util.Date;

/**
 * @author Xyf
 */
public class QDate extends QType<Date> {
    public QDate() {
        this.operator = Operator.RANGE;
    }
}
