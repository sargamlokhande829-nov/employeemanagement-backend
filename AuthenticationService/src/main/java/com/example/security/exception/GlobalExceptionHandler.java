package com.example.security.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import lombok.extern.slf4j.Slf4j;
import java.lang.Exception;

@Slf4j		//enables logs
@ControllerAdvice	// Marks this class as a centralized exception handler
public class GlobalExceptionHandler {

	// Handles all runtime exceptions (e.g., invalid credentials, user not found)
    @ExceptionHandler(RuntimeException.class)
	public ResponseEntity<String> handleRuntimeException(RuntimeException ex, WebRequest request){
		log.warn("Runtime exception: {}",ex.getMessage(),ex);
		System.out.println("Exception is hanndled by GlobalExceptionHandler");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
    
    // Handles all other exceptions (e.g., database errors, null pointer, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong! Try again later.");
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        return  ResponseEntity.badRequest().body(errors);
    }
}

