package com.example.SecureLoginDemo.exception;

public class TooManyLoginAttemptException extends RuntimeException{

    public  TooManyLoginAttemptException(String message){
        super(message);
    }
}
