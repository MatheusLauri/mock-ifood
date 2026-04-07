package com.mockifood.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ProblemDetail handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
    var status = ex.getStatusCode();
    var pd = ProblemDetail.forStatusAndDetail(status, ex.getReason() == null ? "error" : ex.getReason());
    pd.setTitle(status.toString());
    pd.setProperty("timestamp", Instant.now().toString());
    pd.setProperty("path", request.getRequestURI());
    return pd;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    var pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
    pd.setTitle("400 BAD_REQUEST");
    pd.setProperty("timestamp", Instant.now().toString());
    pd.setProperty("path", request.getRequestURI());

    Map<String, Object> fields = new HashMap<>();
    ex.getBindingResult().getFieldErrors()
        .forEach(fe -> fields.put(fe.getField(), fe.getDefaultMessage()));
    pd.setProperty("fields", fields);
    return pd;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
    var pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR");
    pd.setTitle("500 INTERNAL_SERVER_ERROR");
    pd.setProperty("timestamp", Instant.now().toString());
    pd.setProperty("path", request.getRequestURI());
    return pd;
  }
}

