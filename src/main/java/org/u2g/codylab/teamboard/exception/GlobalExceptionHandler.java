package org.u2g.codylab.teamboard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class GlobalExceptionHandler extends RuntimeException {
   @ExceptionHandler(UsernameAlreadyExistsException.class)
   public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex) {
       return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
   }
}
