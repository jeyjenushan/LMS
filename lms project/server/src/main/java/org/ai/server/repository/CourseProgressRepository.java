package org.ai.server.repository;

import org.ai.server.model.CourseEntity;
import org.ai.server.model.CourseProgressEntity;
import org.ai.server.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgressEntity, Integer> {

  Optional<CourseProgressEntity> findByUserAndCourse(UserEntity user, CourseEntity course);
}
