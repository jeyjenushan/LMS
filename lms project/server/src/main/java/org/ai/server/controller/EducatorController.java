package org.ai.server.controller;


import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;

import org.ai.server.service.EducatorService;
import org.ai.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/educator")
@AllArgsConstructor
public class EducatorController {
    private final EducatorService educatorService;
   private final UserService userService;




    @GetMapping("/students")
    public ResponseEntity<Response> getEnrolledStudents(@RequestHeader("Authorization") String authHeader) {

       authHeader=authHeader.substring(7);
        String email= JwtTokenProvider.extractUsername(authHeader);


        Response response = educatorService.getEnrolledStudentsWithPurchaseData(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/dashboard")
    public ResponseEntity<Response> getEducatorDashboard(@RequestHeader("Authorization") String authHeader) {

        authHeader=authHeader.substring(7);
        String email= JwtTokenProvider.extractUsername(authHeader);
        Response response = educatorService.getDashboardData(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);

    }

    @GetMapping("/courses")
    public ResponseEntity<Response> getEducatorCourse( @RequestHeader("Authorization") String token){

        token=token.substring(7);
        String email= JwtTokenProvider.extractUsername(token);
        Response response=educatorService.getEducatorCourse( email);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
