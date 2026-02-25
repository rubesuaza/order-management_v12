package com.example.order_management.domain;

public class CurrencyMismatchException extends DomainException {

    public CurrencyMismatchException(String message) {
        super(message);
    }
}

