package org.ai.server.controller;

import lombok.AllArgsConstructor;
import org.ai.server.configuration.JwtTokenProvider;
import org.ai.server.dto.Response;
import org.ai.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/getDetails")
    public ResponseEntity<Response> getUserData(@RequestHeader("Authorization")
                                                String token
                                                ){
      token=token.substring(7);
        String email=JwtTokenProvider.extractUsername(token);
        Response response=userService.getUserData(email);
        response.setExpirationTime(JwtTokenProvider.extractExpiration(token).toString());
        return ResponseEntity.status(response.getStatusCode()).body(response);

    }




}
