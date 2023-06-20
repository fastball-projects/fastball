package dev.fastball.orm.jpa;

import dev.fastball.core.annotation.Field;
import dev.fastball.core.component.DefaultDataRecord;
import dev.fastball.core.info.basic.DisplayType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class JpaBaseEntity extends DefaultDataRecord {

    @Id
    @GeneratedValue
    @Field(display = DisplayType.Hidden)
    private Long id;

    @CreatedDate
    @Field(display = DisplayType.Hidden)
    @Column(updatable = false)
    private Date createdAt;

    @CreatedBy
    @Field(display = DisplayType.Hidden)
    private Long createdBy;

    @LastModifiedDate
    @Field(display = DisplayType.Hidden)
    private Date lastUpdatedAt;

    @LastModifiedBy
    @Field(display = DisplayType.Hidden)
    private Long lastUpdatedBy;
}

