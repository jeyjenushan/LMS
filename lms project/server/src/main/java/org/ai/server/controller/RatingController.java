package org.ai.server.controller;

import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.model.UserEntity;
import org.ai.server.repository.UserRepository;
import org.ai.server.request.RatingRequest;
import org.ai.server.service.RatingService;
import org.ai.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// RatingController.java
@RestController
@RequestMapping("/api/ratings")
@AllArgsConstructor
public class RatingController {
    private final RatingService ratingService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> addRating(@RequestBody RatingRequest request,
                                       @RequestHeader("Authorization") String authHeader) {


            String email=userService.getUserDataWithToken(authHeader);

            Response response=ratingService.addRating(
                   email,
                    request.getCourseId(),
                    request.getRating()
            );
            return ResponseEntity.status(response.getStatusCode()).body(response);

    }}