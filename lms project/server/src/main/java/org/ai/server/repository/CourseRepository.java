package org.ai.server.repository;
import org.ai.server.model.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CourseRepository extends JpaRepository<CourseEntity,Long> {
    List<CourseEntity> findByIsPublished(boolean b);
    List<CourseEntity> findByEducatorId(long educatorId);


}
