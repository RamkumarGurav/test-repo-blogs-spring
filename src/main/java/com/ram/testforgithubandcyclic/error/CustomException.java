package com.ram.testforgithubandcyclic.error;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException{

    private  HttpStatus httpStatus;

    public CustomException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public CustomException(String message,HttpStatus httpStatus) {
        super(message);
        this.httpStatus=httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }




    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
    public CustomException(Throwable cause,HttpStatus httpStatus) {
        super(cause);
        this.httpStatus=httpStatus;
    }

    protected CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
