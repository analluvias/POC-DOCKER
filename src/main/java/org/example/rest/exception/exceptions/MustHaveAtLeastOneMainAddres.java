package org.example.rest.exception.exceptions;

public class MustHaveAtLeastOneMainAddres extends RuntimeException {
    public MustHaveAtLeastOneMainAddres() {
        super("Customer must have at least one main address");
    }
}
