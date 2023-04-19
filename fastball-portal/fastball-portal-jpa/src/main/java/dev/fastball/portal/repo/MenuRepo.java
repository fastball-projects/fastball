package dev.fastball.portal.repo;

import dev.fastball.portal.entity.JpaMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepo extends JpaRepository<JpaMenuEntity, Long> {
}
