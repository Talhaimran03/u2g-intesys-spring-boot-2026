package org.u2g.codylab.teamboard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.u2g.codylab.teamboard.dto.ErrorResponseApiDTO;

@Getter
public class ErrorResponseException extends org.springframework.web.ErrorResponseException {

    private final ErrorResponseApiDTO errorResponse;

    public ErrorResponseException(ErrorResponseApiDTO errorResponse) {
        super(HttpStatus.valueOf(errorResponse.getStatus()));
        this.errorResponse = errorResponse;
    }

}
