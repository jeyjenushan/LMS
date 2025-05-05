package org.ai.server.service;

import org.ai.server.dto.Response;
import org.ai.server.model.CourseProgressEntity;
import org.ai.server.repository.CourseProgressRepository;
import org.springframework.stereotype.Service;

@Service
public interface CourseProgressService {

    Response updateCourseProgress(String email, Long lectureId);
    Response getCourseProgress(String email, Long courseId);
}
