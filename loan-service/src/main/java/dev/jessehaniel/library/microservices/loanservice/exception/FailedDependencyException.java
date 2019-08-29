package dev.jessehaniel.library.microservices.loanservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FAILED_DEPENDENCY)
public class FailedDependencyException extends RuntimeException {
    private static final long serialVersionUID = 7076892633373495341L;
    
    public FailedDependencyException(String message) {
        super(message);
    }
}
