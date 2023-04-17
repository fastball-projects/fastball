package dev.fastball.portal.entity;

import dev.fastball.orm.jpa.JpaBaseEntity;
import dev.fastball.orm.jpa.converter.MapJsonConverter;
import dev.fastball.portal.model.entity.MenuEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "fb_menu")
@EqualsAndHashCode(callSuper = true)
public class JpaMenuEntity extends JpaBaseEntity implements MenuEntity {

    private String parentId;
    private String code;
    private String name;
    private String path;
    @Convert(converter = MapJsonConverter.class)
    private Map<String, Object> params;
    private String description;
}
