package org.example.rest.exception.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import org.example.rest.exception.exceptionDto.ExceptionDto;
import org.example.rest.exception.exceptions.DocumentInUseException;
import org.example.rest.exception.exceptions.EmailInUseException;
import org.example.rest.exception.exceptions.EqualValueException;
import org.example.rest.exception.exceptions.InvalidCustomerTypeException;
import org.example.rest.exception.exceptions.MustHaveAtLeastOneMainAddres;
import org.example.rest.exception.exceptions.ObjectNotFoundException;
import org.example.rest.exception.exceptions.PhoneNumberInUseException;
import org.example.rest.exception.exceptions.TooManyAddressesException;
import org.example.rest.exception.exceptions.TooManyMainAddressesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(DocumentInUseException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> handleExistingConsumerException(DocumentInUseException ex, HttpServletRequest request){
        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getRequestURI());


        return ResponseEntity.status(HttpStatus.CONFLICT).body(  exceptionDto  );
    }

    @ExceptionHandler(TooManyMainAddressesException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> tooManyMainAddressException(TooManyMainAddressesException ex, HttpServletRequest request){
        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> constraintViolationException(ConstraintViolationException ex, HttpServletRequest request){

        String messageFinal = null;

        Pattern p = Pattern.compile( "messageTemplate='(.[^']*)" );
        String message = ex.getLocalizedMessage();
        Matcher m = p.matcher(message);

        if ( m.find() ) {
            messageFinal = m.group(1);
        }

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                messageFinal,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(MustHaveAtLeastOneMainAddres.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> mustHaveAtLeastOneMainAddres(MustHaveAtLeastOneMainAddres ex, HttpServletRequest request){

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(TooManyAddressesException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> tooManyAddresses(TooManyAddressesException ex, HttpServletRequest request){

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> MethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request){

        String messageFinal = null;

        Pattern p = Pattern.compile( "]]; default message \\[(.[^']*)" );
        String message = ex.getMessage();
        Matcher m = p.matcher(message);

        if ( m.find() ) {
            messageFinal = m.group(1);
        }

        messageFinal = messageFinal.substring(0, messageFinal.length() - 3);

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                messageFinal,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(com.fasterxml.jackson.databind.exc.InvalidFormatException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> invalidFormatException(com.fasterxml.jackson.databind.exc.InvalidFormatException ex, HttpServletRequest request){

        String messageFinal = null;

        Pattern p = Pattern.compile( "String (.[^'\n]*)" );
        String message = ex.getMessage();
        Matcher m = p.matcher(message);

        if ( m.find() ) {
            messageFinal = m.group(1);
        }

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                messageFinal,
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> objectNotFoundException(ObjectNotFoundException ex, HttpServletRequest request){

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(EmailInUseException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> emailInUseException(EmailInUseException ex, HttpServletRequest request){

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(PhoneNumberInUseException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> phoneNumberInUseException(PhoneNumberInUseException ex, HttpServletRequest request){

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(InvalidCustomerTypeException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> invalidCustomerTypeException(InvalidCustomerTypeException ex, HttpServletRequest request){

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

    @ExceptionHandler(EqualValueException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> equalValueException(EqualValueException ex, HttpServletRequest request){

        ExceptionDto exceptionDto = new ExceptionDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(  exceptionDto  );
    }

}
