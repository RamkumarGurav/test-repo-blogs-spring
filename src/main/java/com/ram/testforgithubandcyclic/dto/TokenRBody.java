package com.ram.testforgithubandcyclic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRBody<T> {

    private String status;
    private String token;
    private T data;

}