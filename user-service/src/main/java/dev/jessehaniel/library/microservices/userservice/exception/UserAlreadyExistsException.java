package dev.jessehaniel.library.microservices.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "User ID already exists";
    
    public UserAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }
}
