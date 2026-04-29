package edu.eci.patricia.domain.exceptions;

public class ServiceUnavailableException extends RuntimeException{

    public ServiceUnavailableException(String message, Throwable cause){
        super(message,cause);
    }
}