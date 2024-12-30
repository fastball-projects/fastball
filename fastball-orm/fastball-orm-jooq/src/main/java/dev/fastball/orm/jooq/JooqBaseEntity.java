package dev.fastball.orm.jooq;

import dev.fastball.core.annotation.Field;
import dev.fastball.core.component.DefaultDataRecord;
import dev.fastball.meta.basic.DisplayType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author Geng Rong
 */
@Getter
@Setter
public abstract class JooqBaseEntity extends DefaultDataRecord {

    @Field(display = DisplayType.Hidden)
    private Long id;

    @Field(display = DisplayType.Hidden)
    private Date createdAt;

    @Field(display = DisplayType.Hidden)
    private Long createdBy;

    @Field(display = DisplayType.Hidden)
    private Date lastUpdatedAt;

    @Field(display = DisplayType.Hidden)
    private Long lastUpdatedBy;
}