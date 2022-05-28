package com.sparta.demo.exception;

public class ExistSessionException extends Exception{
    public ExistSessionException(String userName){
        super(userName + "is already exist");
    }
}
