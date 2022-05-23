package com.sparta.demo.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResult {
    private Boolean status;
    private String msg;
}
