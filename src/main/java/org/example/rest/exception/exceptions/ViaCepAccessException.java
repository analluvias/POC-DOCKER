package org.example.rest.exception.exceptions;

import java.io.IOException;

public class ViaCepAccessException extends RuntimeException{
    public ViaCepAccessException(Exception e) {
        super(e.getMessage());
    }
}
