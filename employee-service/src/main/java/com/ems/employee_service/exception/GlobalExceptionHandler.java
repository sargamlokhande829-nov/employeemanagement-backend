package com.ems.employee_service.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(Map.of(
                                 "status", HttpStatus.BAD_REQUEST.value(),
                                 "error", "Bad Request",
                                 "message", ex.getMessage()
                             ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(Map.of(
                                 "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                 "error", "Internal Server Error",
                                 "message", ex.getMessage()
                             ));
    }
}
