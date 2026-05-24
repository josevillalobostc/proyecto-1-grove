package com.app.grove.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
        public ProblemDetail notStockHandler(ResourceNotFoundException ex){
            ProblemDetail pD = ProblemDetail.forStatus(409);
            pD.setTitle("Insufficient stock");
            pD.setDetail(ex.getMessage());
            return pD;
        }
}
