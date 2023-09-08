package com.ram.testforgithubandcyclic.controller;


import com.ram.testforgithubandcyclic.error.CustomException;
import com.ram.testforgithubandcyclic.error.FormValidationException;
import com.ram.testforgithubandcyclic.error.NotFoundException;
import com.ram.testforgithubandcyclic.dto.ErrorResponseDev;
import com.ram.testforgithubandcyclic.dto.ErrorResponseProd;
import com.ram.testforgithubandcyclic.utilities.ProfileComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice //used to define a global exception handler for  application
public class RestResponseExecptionHandler {


    @Autowired
    private ProfileComponent profileComponent;


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> customExceptionHandler(CustomException ex, WebRequest request) {
        String activeProfile = profileComponent.getActiveProfiles()[0];


        if ("dev".equalsIgnoreCase(activeProfile) ) {
            // For dev profile, include error stack trace
            ErrorResponseDev message = new ErrorResponseDev("failed", ex.getMessage(), Arrays.stream(ex.getStackTrace())
                    .map(track -> track.toString()).collect(Collectors.toList()));
            return ResponseEntity.status(ex.getHttpStatus()).body(message);


        } else {
            // For prod profile, include only error message

            ErrorResponseProd message = new ErrorResponseProd("failed", ex.getMessage());
            return ResponseEntity.status(ex.getHttpStatus()).body(message);

        }


    }


    @ExceptionHandler({NotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<Object> notFoundException(NotFoundException ex) {
        String activeProfile = profileComponent.getActiveProfiles()[0]; // Get the active profile


        if ("dev".equalsIgnoreCase(activeProfile) ) {
            // For dev profile, include error stack trace
            ErrorResponseDev message = new ErrorResponseDev("failed", ex.getMessage(), Arrays.stream(ex.getStackTrace())
                    .map(track -> track.toString()).collect(Collectors.toList()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);


        } else {
            // For prod profile, include only error message

            ErrorResponseProd message = new ErrorResponseProd("failed", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);

        }
    }


//    @ExceptionHandler(ValidationException.class)
//    public ResponseEntity<ErrorMessage> validationExceptionHandler(ValidationException exception, WebRequest request){
//
//        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST,exception.getMessage());
//
//        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
//    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        String activeProfile = profileComponent.getActiveProfiles()[0];

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        log.info("MethodArgumentNotValidException");

//        ErrorResponseDev errorResponse = new ErrorResponseDev("failed", errors);
//        return ResponseEntity.badRequest().body(errorResponse);

        if ("dev".equalsIgnoreCase(activeProfile) ) {
            // For dev profile, include error stack trace
            ErrorResponseDev message = new ErrorResponseDev("failed", errors, Arrays.stream(ex.getStackTrace())
                    .map(track -> track.toString()).collect(Collectors.toList()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);


        } else {
            // For prod profile, include only error message

            ErrorResponseProd message = new ErrorResponseProd("failed", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);

        }
    }


    @ExceptionHandler(FormValidationException.class)
    public ResponseEntity<Object> handleFormValidationException(FormValidationException ex) {
        String activeProfile = profileComponent.getActiveProfiles()[0];

        String errorMsg = ex.getMessage();

        log.info("MethodArgumentNotValidException");

//        ErrorResponseDev errorResponse = new ErrorResponseDev("failed", errorMsg,Arrays.stream(ex.getStackTrace()));
//        return ResponseEntity.badRequest().body(errorResponse);

        if ("dev".equalsIgnoreCase(activeProfile) || "qa".equalsIgnoreCase(activeProfile)) {
            // For dev profile, include error stack trace
            ErrorResponseDev message = new ErrorResponseDev("failed", ex.getMessage(), Arrays.stream(ex.getStackTrace())
                    .map(track -> track.toString()).collect(Collectors.toList()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);


        } else {
            // For prod profile, include only error message

            ErrorResponseProd message = new ErrorResponseProd("failed", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);

        }
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingParam(MissingServletRequestParameterException ex) {

        String activeProfile = profileComponent.getActiveProfiles()[0];

        String paramName = ex.getParameterName();
        String errorMessage = "Required parameter '" + paramName + "' is missing.";
        ErrorResponseDev errorResponse = new ErrorResponseDev("error", errorMessage, Arrays.stream(ex.getStackTrace())
                .map(track -> track.toString()).collect(Collectors.toList()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }



    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String activeProfile = profileComponent.getActiveProfiles()[0]; // Get the active profile


        if ("dev".equalsIgnoreCase(activeProfile) ) {
            // For dev profile, include error stack trace
            ErrorResponseDev message = new ErrorResponseDev("failed", ex.getMessage(), Arrays.stream(ex.getStackTrace())
                    .map(track -> track.toString()).collect(Collectors.toList()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);


        } else {
            // For prod profile, include only error message

            ErrorResponseProd message = new ErrorResponseProd("failed", "Request Body is missing");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);

        }
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        String activeProfile = profileComponent.getActiveProfiles()[0]; // Get the active profile


        if ("dev".equalsIgnoreCase(activeProfile) ) {
            // For dev profile, include error stack trace
            ErrorResponseDev message = new ErrorResponseDev("failed", ex.getMessage()+" for this request URL", Arrays.stream(ex.getStackTrace())
                    .map(track -> track.toString()).collect(Collectors.toList()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);


        } else {
            // For prod profile, include only error message

            ErrorResponseProd message = new ErrorResponseProd("failed", ex.getMessage()+" for this request URL");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);

        }
    }




    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> finalExceptionHandler(Exception ex, WebRequest request) {
        String activeProfile = profileComponent.getActiveProfiles()[0];

        if ("dev".equalsIgnoreCase(activeProfile) ) {
            // For dev profile, include error stack trace
            ErrorResponseDev message = new ErrorResponseDev("error", ex.getMessage()+" for this request URL", Arrays.stream(ex.getStackTrace())
                    .map(track -> track.toString()).collect(Collectors.toList()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);


        } else {
            // For prod profile, include only error message

            ErrorResponseProd message = new ErrorResponseProd("error", ex.getMessage()+" for this request URL");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);

        }


    }

}


/**
 * The code you've provided is a Spring @ControllerAdvice class that handles exceptions in your REST API by providing custom responses. In this case, it's handling the NotFoundException and returning an appropriate response.
 * <p>
 * Here's a breakdown of what the code does:
 *
 * @ControllerAdvice: This annotation indicates that the class is an exception handler that applies
 * globally to all @RestController classes in your application.
 * @ExceptionHandler(NotFoundException.class): This annotation defines a method that will handle
 * exceptions of the specified type (NotFoundException in this case).
 * <p>
 * public ResponseEntity<ErrorMessage> notFoundException(NotFoundException exception, WebRequest request):
 * This method is the exception handler for NotFoundException. It takes two parameters: the exception itself and the WebRequest that holds the request details.
 * <p>
 * Inside the method, an ErrorMessage object is created. This is a custom class (not provided in your code snippet)
 * that likely holds information about the error, such as the HTTP status code and a message.
 * <p>
 * The method returns a ResponseEntity containing the custom error message and an HTTP status of NOT_FOUND (404).
 * <p>
 * Overall, this code sets up a global exception handling mechanism for the NotFoundException in your REST API.
 * When a NotFoundException is thrown anywhere in your API, this handler will catch it and return a custom error response
 * with a 404 Not Found status and a message. This helps improve the user experience by providing consistent and meaningful error responses.
 */




