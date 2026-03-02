package org.u2g.codylab.teamboard.exception;

import org.springframework.http.HttpStatus;
import org.u2g.codylab.teamboard.dto.ErrorResponseApiDTO;

import java.time.OffsetDateTime;

public class CustomAnauthorizedException extends ErrorResponseException {

    public CustomAnauthorizedException(String message) {
        super(asErrorResponseApiDTO(message));
    }

    private static ErrorResponseApiDTO asErrorResponseApiDTO(String message) {
        return new ErrorResponseApiDTO()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(message);
    }
}
