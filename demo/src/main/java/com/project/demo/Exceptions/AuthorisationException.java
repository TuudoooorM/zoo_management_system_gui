package com.project.demo.Exceptions;

public class AuthorisationException extends Exception {
    public AuthorisationException(String message) {
        super("[AUTHORISATION ERROR]: " + message);
    }
}
