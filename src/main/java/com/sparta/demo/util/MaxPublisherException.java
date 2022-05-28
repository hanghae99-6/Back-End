package com.sparta.demo.util;

import org.apache.http.HttpResponse;

public class MaxPublisherException extends Exception{
    public MaxPublisherException(HttpResponse response){
        response.setStatusCode(409);
    }
}
