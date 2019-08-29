package dev.jessehaniel.library.microservices.bookservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookAlreadyExistsException extends RuntimeException {
    
    private static final String DEFAULT_MESSAGE = "Book ID already exists";
    
    public BookAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }
}
