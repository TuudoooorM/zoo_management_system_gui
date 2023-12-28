package com.project.demo.Exceptions;

public class EnclosureCapacityExceededException extends Exception {
    public EnclosureCapacityExceededException(String message) {
        super("[ENCLOSURE CAPACITY ERROR]: " + message);
    }
}
