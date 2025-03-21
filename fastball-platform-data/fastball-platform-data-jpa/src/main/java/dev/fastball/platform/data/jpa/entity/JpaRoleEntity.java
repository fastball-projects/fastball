package dev.fastball.platform.data.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.fastball.core.annotation.Field;
import dev.fastball.orm.jpa.JpaBaseEntity;
import dev.fastball.platform.entity.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fb_role")
@EqualsAndHashCode(callSuper = true)
public class JpaRoleEntity extends JpaBaseEntity implements Role {

    @Field(title = "角色编码")
    private String code;
    @Field(title = "角色名称")
    private String name;
    @Field(title = "角色描述")
    private String description;


    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "fb_role_permission_rel", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<JpaPermissionEntity> permissions;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<JpaUserEntity> users;
}
