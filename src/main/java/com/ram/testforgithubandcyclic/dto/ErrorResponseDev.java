package com.ram.testforgithubandcyclic.dto;



import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class ErrorResponseDev<T> {

    private String status;
    private T message;

    private T stack;

    public ErrorResponseDev(String status, T error) {
        this.status = status;
        this.message = message;
    }

    public ErrorResponseDev(String status, T message, T stack) {
        this.status = status;
        this.message = message;
        this.stack = stack;
    }
}

