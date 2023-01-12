package org.example.rest.exception.exceptions;

public class CepShouldHaveStateAndCityException extends RuntimeException {

    public CepShouldHaveStateAndCityException() {
        super("Cep should return at least state and city.");
    }
}
