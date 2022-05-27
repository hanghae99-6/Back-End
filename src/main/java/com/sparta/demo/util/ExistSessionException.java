package com.sparta.demo.util;

public class ExistSessionException extends Exception{
    public ExistSessionException(String userName){
        super(userName + "is already exist");
    }
}
