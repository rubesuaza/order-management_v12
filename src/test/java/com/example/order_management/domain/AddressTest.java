package com.example.order_management.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AddressTest {

    @Test
    @DisplayName("No permite crear Address con campos vacíos o nulos")
    void shouldNotAllowInvalidAddress() {
        assertThatThrownBy(() -> new Address(null, "City", "12345", "Country"))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> new Address("Street", "", "12345", "Country"))
                .isInstanceOf(DomainException.class);

        assertThatThrownBy(() -> new Address("Street", "City", "   ", "Country"))
                .isInstanceOf(DomainException.class);
    }
}

