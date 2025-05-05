package org.ai.server.controller;

import org.ai.server.dto.Response;
import org.ai.server.exception.CourseOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CourseOperationException.class)
    public ResponseEntity<Response> handleCourseOperationException(CourseOperationException ex) {

        Response response = new Response();
        response.setMessage(ex.getMessage());
        response.setSuccess(false);
        response.setStatusCode(500); // or appropriate status code
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleGenericException(Exception ex) {
        Response response = new Response();
        response.setMessage(ex.getMessage());
        response.setStatusCode(500);
        response.setSuccess(false);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
