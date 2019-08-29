package dev.jessehaniel.library.microservices.bookservice.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
class ExceptionResponse {
    private LocalDateTime timeOccurrence = LocalDateTime.now();
    private String exceptionMessage;
    
    ExceptionResponse(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
