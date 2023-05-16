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

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class JpaBaseEntity extends DefaultDataRecord {

    @Id
    @GeneratedValue
    @Field(display = DisplayType.Hidden)
    private Long id;

    @CreatedDate
    @Field(display = DisplayType.Hidden)
    private Date createdAt;

    @CreatedBy
    @Field(display = DisplayType.Hidden)
    private String createdBy;

    @LastModifiedDate
    @Field(display = DisplayType.Hidden)
    private Date lastUpdatedAt;

    @LastModifiedBy
    @Field(display = DisplayType.Hidden)
    private String lastUpdatedBy;
}

