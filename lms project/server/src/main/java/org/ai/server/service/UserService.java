package org.ai.server.service;

import org.ai.server.dto.Response;

public interface UserService {
    Response getUserData(String email);
    String getUserDataWithToken(String token);
    Response getEnrolledCourses(String email);
}
