package org.ai.server.repository;

import org.ai.server.model.CourseEntity;
import org.ai.server.model.CourseRatingEntity;
import org.ai.server.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRatingRepository extends JpaRepository<CourseRatingEntity,Long> {
    Optional<CourseRatingEntity> findByUserAndCourse(UserEntity user, CourseEntity course);

    List<CourseRatingEntity> findByCourse(CourseEntity course);
}
