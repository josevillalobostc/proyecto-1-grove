package com.app.grove.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ProblemDetail resourceNotFoundException(ResourceNotFoundException ex){
        ProblemDetail pD = ProblemDetail.forStatus(404);
        pD.setTitle("Resource not found");
        pD.setDetail(ex.getMessage());
        return pD;
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public ProblemDetail userExistsHandler(UserAlreadyExistsException ex){
        ProblemDetail pD = ProblemDetail.forStatus(409);
        pD.setTitle("User already exists");
        pD.setDetail(ex.getMessage());
        return pD;
    }
}
