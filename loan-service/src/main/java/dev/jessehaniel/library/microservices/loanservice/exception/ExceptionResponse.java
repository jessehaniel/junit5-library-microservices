package dev.jessehaniel.library.microservices.loanservice.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionResponse {
    private LocalDateTime timeOccurrence = LocalDateTime.now();
    private String exceptionMessage;
    private String details;
    
    public ExceptionResponse(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
