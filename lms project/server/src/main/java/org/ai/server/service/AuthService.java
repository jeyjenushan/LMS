package org.ai.server.service;

import org.ai.server.dto.Response;
import org.ai.server.model.UserEntity;
import org.ai.server.request.LoginRequest;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {
    Response LoginUser(LoginRequest loginRequest);

    Response RegisterUser(UserEntity user, MultipartFile imageFile);
}
