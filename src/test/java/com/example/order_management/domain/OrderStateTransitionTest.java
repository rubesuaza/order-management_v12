package com.example.order_management.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderStateTransitionTest {

    @Test
    @DisplayName("No permite cancelar una orden ya entregada")
    void shouldNotAllowCancelDeliveredOrder() {
        UUID customerId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, unitPrice);

        Order order = Order.create(customerId, List.of(item));
        order.markAsPaid();
        order.ship();
        order.markAsDelivered();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidOrderStateException.class);
    }
}

