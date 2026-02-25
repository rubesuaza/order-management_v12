package com.example.order_management.domain;

public class InvalidItemException extends DomainException {

    public InvalidItemException(String message) {
        super(message);
    }
}

