package org.example.rest.exception.exceptions;

public class MustHaveAtLeastOneMainAddres extends RuntimeException {
    public MustHaveAtLeastOneMainAddres() {
        super("You must have at least one main address");
    }
}
