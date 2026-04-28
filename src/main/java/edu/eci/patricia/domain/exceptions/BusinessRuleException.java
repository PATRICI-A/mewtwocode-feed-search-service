package edu.eci.patricia.domain.exceptions;

public class BusinessRuleException extends RuntimeException{

    public BusinessRuleException(String message){
        super(message);
    }
}