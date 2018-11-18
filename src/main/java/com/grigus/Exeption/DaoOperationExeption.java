package com.grigus.Exeption;

public class DaoOperationExeption extends RuntimeException {
    public DaoOperationExeption(String message) {
        super(message);
    }

    public DaoOperationExeption(String message,Throwable cause) {
        super(message,cause);
    }
}
