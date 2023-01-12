package org.example.rest.exception.exceptions;

public class NonExistentCepException extends RuntimeException{

    public NonExistentCepException() {
        super("The CEP you choose does not exist. Check if you typed it correctly.");
    }
}
