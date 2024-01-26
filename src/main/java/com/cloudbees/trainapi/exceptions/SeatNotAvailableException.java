package com.cloudbees.trainapi.exceptions;

public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String msg,Exception e) {
        super(msg,e);
    }
    public SeatNotAvailableException(String msg) {
        super(msg);
    }
}
