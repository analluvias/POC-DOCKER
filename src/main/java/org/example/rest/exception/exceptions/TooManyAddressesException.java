package org.example.rest.exception.exceptions;

public class TooManyAddressesException extends RuntimeException{

    public TooManyAddressesException() {
        super("customer can only have 5 addresses.");
    }
}
