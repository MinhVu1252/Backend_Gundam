package com.joyboy.commonservice.common.exceptions;

import com.joyboy.commonservice.common.response.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseObject> handleValidationException(ValidationException ex) {
        BindingResult result = ex.getBindingResult();
        List<String> errorMessages = result.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(ResponseObject.builder()
                .message(String.join(", ", errorMessages))
                .status(HttpStatus.BAD_REQUEST)
                .data(null)
                .build());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseObject> handleGeneralException(Exception exception) {
        return ResponseEntity.internalServerError().body(
                ResponseObject.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ExistsException.class)
    public ResponseEntity<ResponseObject> handleExistsException(ExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseObject.builder()
                        .status(HttpStatus.CONFLICT)
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseObject.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build());
    }

    @ExceptionHandler(DataNullException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> handleResourceNullException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseObject.builder()
                .status(HttpStatus.NO_CONTENT)
                .message(exception.getMessage())
                .build());
    }

}