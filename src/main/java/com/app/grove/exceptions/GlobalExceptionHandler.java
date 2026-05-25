package com.app.grove.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(404);
        pd.setTitle("Resource not found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(409);
        pd.setTitle("User already exists");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ProblemDetail handleUsernameNotFound(UsernameNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(404);
        pd.setTitle("User not found");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(DuplicateResourceException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(409);
        pd.setTitle("Duplicate resource");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(InvalidOperationException.class)
    public ProblemDetail handleInvalidOperation(InvalidOperationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(400);
        pd.setTitle("Invalid operation");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(403);
        pd.setTitle("Forbidden");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(BadRequestException.class)
    public ProblemDetail handleBadRequest(BadRequestException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(400);
        pd.setTitle("Bad request");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    @ExceptionHandler(AlreadyMemberException.class)
    public ProblemDetail handleAlreadyMember(AlreadyMemberException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(409);
        pd.setTitle("Already a member");
        pd.setDetail(ex.getMessage());
        return pd;
    }
}
