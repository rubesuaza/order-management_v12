package com.example.order_management.domain;

public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}

