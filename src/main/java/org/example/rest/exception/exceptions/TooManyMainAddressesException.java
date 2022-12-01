package org.example.rest.exception.exceptions;

public class TooManyMainAddressesException extends RuntimeException {
    public TooManyMainAddressesException() {
        super("You can only have one main address.");
    }
}
