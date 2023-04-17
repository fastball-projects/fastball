package dev.fastball.orm.jpa;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class JpaBaseEntity {

    @Id
    private String id;

    @CreatedDate
    private Date createdAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private Date lastUpdatedAt;

    @LastModifiedBy
    private String lastUpdatedBy;
}
