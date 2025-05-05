package org.ai.server.service;

import org.ai.server.dto.Response;
import org.springframework.web.multipart.MultipartFile;

public interface CourseControllerService {
    Response getAllCourses();

    Response getCourseById(Long id);

    Response addCourse(String courseDataJson, MultipartFile imageFile,String email);
}
