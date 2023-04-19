package dev.fastball.portal.repo;

import dev.fastball.portal.entity.JpaPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepo extends JpaRepository<JpaPermissionEntity, Long> {

    @Query(value = "SELECT p.* FROM fb_permission p where p.id in (select permission_id from fb_role_permission_rel where role_id in (select role_id from fb_user_role_rel where user_id = ?1))", nativeQuery = true)
    List<JpaPermissionEntity> findByUserId(Long userId);

    @Query(value = "SELECT p.* FROM fb_permission p where p.type = ?2 and p.id in (select permission_id from fb_role_permission_rel where role_id in (select role_id from fb_user_role_rel where user_id = ?1))", nativeQuery = true)
    List<JpaPermissionEntity> findByUserIdAndType(Long userId, String permissionType);
}
