package org.u2g.codylab.teamboard.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super("username already exists: " + message);
    }
}
