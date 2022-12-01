package org.example.rest.exception.exceptions;

public class DocumentInUseException extends RuntimeException{

    public DocumentInUseException(){
        super("This document is already in use.");
    }

}
