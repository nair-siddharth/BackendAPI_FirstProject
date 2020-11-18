package com.upgrad.proman.service.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class AuthenticationFailedException extends Exception {

    private final String code;
    private final String errorMessage;

    public AuthenticationFailedException(String code, String errorMessage){
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public void printStackTrace(){
        super.printStackTrace();
    }
    @Override
    public void printStackTrace(PrintStream s){
        super.printStackTrace(s);
    }
    @Override
    public void printStackTrace(PrintWriter w){
        super.printStackTrace(w);
    }


    public String getCode(){
        return this.code;
    }
    public String getErrorMessage(){
        return this.errorMessage;
    }

}
