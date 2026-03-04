package org.u2g.codylab.teamboard.exception;

import org.springframework.http.HttpStatus;
import org.u2g.codylab.teamboard.dto.ErrorResponseApiDTO;

import java.time.OffsetDateTime;

public class CustomConflictException extends ErrorResponseException {

    public CustomConflictException(String message) {
        super(asErrorResponseApiDTO(message));
    }

    private static ErrorResponseApiDTO asErrorResponseApiDTO(String message) {
        return new ErrorResponseApiDTO()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .message(message);
    }
}
