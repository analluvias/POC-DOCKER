package org.example.rest.exception.exceptions;

public class InvalidCustomerTypeException extends RuntimeException{
    public InvalidCustomerTypeException() {
        super("Invalid customer type, should be FISICA or JURIDICA");
    }
}
