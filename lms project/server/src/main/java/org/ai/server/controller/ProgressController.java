package org.ai.server.controller;

import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.model.CourseProgressEntity;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.UserRepository;
import org.ai.server.request.ProgressRequest;
import org.ai.server.service.CourseProgressService;
import org.ai.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@AllArgsConstructor
public class ProgressController {

    private final UserRepository userRepository;
    private final CourseProgressService courseProgressService;
    private final UserService userService;



    @GetMapping("/enrolled")
    public ResponseEntity<Response> getUserEnrolledCourses(@RequestHeader("Authorization") String authHeader) {


        String email=userService.getUserDataWithToken(authHeader);
        Response response=userService.getEnrolledCourses(email);
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @PostMapping("/updateCourse")
    public ResponseEntity<Response> updateCourseProgress(@RequestBody ProgressRequest request,
                                                         @RequestHeader("Authorization") String authHeader) {

        String email=userService.getUserDataWithToken(authHeader);

        Response response=courseProgressService.updateCourseProgress(
               email,
                request.getLectureId()
        );
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/getCourse")
    public ResponseEntity<Response> getCourseProgress(@RequestParam Long courseId,
                                               @RequestHeader("Authorization") String authHeader) {


        String email=userService.getUserDataWithToken(authHeader);

        Response response=courseProgressService.getCourseProgress(
                email,
          courseId
        );
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
