package org.example.rest.exception.exceptions;

public class EmailInUseException extends RuntimeException{

    public EmailInUseException() {
        super("This email is already in use. choose another one.");
    }
}
