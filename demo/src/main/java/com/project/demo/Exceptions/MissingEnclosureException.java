package com.project.demo.Exceptions;

public class MissingEnclosureException extends Exception {
    public MissingEnclosureException(String message) {
        super("[MISSING ENCLOSURE]: " + message);
    }
}
