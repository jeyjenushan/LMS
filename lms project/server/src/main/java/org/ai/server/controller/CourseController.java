package org.ai.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;

import org.ai.server.service.CourseControllerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/course")
@AllArgsConstructor
public class CourseController {


    private final CourseControllerService courseControllerService;



    @GetMapping("/all")
    public ResponseEntity<Response> getAllCourses(){
        Response response=courseControllerService.getAllCourses();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCourseById(@PathVariable("id") Long id){
        Response response=courseControllerService.getCourseById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/add-course")
    public ResponseEntity<Response>addCourse(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("courseData") String courseDataJson,
            @RequestPart("image") MultipartFile imageFile
    ){

        String token = authHeader.startsWith("Bearer ") ?
                authHeader.substring(7) :
                authHeader;

        String email= JwtTokenProvider.extractUsername(token);
        Response response=courseControllerService.addCourse(courseDataJson,imageFile,email);
        response.setToken(token);
        response.setExpirationTime(JwtTokenProvider.extractExpiration(token).toString());
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }
}
