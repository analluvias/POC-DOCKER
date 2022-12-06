package org.example.rest.exception.exceptions;

public class PhoneNumberInUseException extends RuntimeException {
    public PhoneNumberInUseException() {
        super("This phone number is alreaydy in use. choose another one.");
    }
}
