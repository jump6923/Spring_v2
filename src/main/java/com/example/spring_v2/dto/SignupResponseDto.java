package com.example.spring_v2.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SignupResponseDto {
    private String msg;
    private int statusCode;

    public SignupResponseDto(String msg, int statusCode){
        this.msg = msg;
        this.statusCode = statusCode;
    }
}