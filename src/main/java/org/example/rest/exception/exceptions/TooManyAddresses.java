package org.example.rest.exception.exceptions;

public class TooManyAddresses extends RuntimeException{

    public TooManyAddresses() {
        super("customer can only have 5 addresses.");
    }
}
