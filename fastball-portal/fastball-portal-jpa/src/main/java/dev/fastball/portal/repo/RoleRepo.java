package dev.fastball.portal.repo;

import dev.fastball.portal.entity.JpaRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<JpaRoleEntity, Long> {
}
