package com.project.demo.Exceptions;

public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super("[AUTHENTICATION ERROR]: " + message);
    }
}
