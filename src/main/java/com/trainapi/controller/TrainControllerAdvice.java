package com.trainapi.controller;

import com.trainapi.exceptions.ResourceNotFoundException;
import com.trainapi.exceptions.SeatNotAvailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
@Slf4j
class TrainControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("response",e.getMessage()));
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<?> handleSeatsNotAvailableException(SeatNotAvailableException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("response",e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("response",e.getMessage()));
    }
}

