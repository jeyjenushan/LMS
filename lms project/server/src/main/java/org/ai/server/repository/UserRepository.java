package org.ai.server.repository;

import org.ai.server.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);
}
