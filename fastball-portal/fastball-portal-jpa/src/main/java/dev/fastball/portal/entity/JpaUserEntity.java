package dev.fastball.portal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.fastball.orm.jpa.JpaBaseEntity;
import dev.fastball.portal.dict.UserStatus;
import dev.fastball.portal.model.entity.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "fb_user")
@EqualsAndHashCode(callSuper = true)
public class JpaUserEntity extends JpaBaseEntity implements UserEntity {

    private String mobile;
    private String username;
    private String password;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "fb_user_role_rel", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<JpaRoleEntity> roles;
}
