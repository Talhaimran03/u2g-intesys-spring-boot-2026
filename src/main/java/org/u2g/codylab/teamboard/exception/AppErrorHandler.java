package org.u2g.codylab.teamboard.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.u2g.codylab.teamboard.dto.ErrorResponseApiDTO;

import java.time.OffsetDateTime;

@Slf4j
@RestControllerAdvice
public class AppErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ErrorResponseApiDTO> handleProjectNotFound(ErrorResponseException ex) {

        log.error("ErrorResponseException: {}", ex.getErrorResponse(), ex);
        return new ResponseEntity<>(ex.getErrorResponse(), HttpStatus.valueOf(ex.getErrorResponse().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseApiDTO> handleProjectNotFound(Exception ex) {

        log.error("ErrorResponseException: {}", ex, ex);

        ErrorResponseApiDTO errorResponse = new ErrorResponseApiDTO()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal Server Error");

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatus()));
    }

}

