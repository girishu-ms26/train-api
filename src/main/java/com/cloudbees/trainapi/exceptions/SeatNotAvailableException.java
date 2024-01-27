package com.cloudbees.trainapi.exceptions;

public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String msg) {
        super(msg);
    }
}
