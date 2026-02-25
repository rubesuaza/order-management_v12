package com.example.order_management.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderItemTest {

    @Test
    @DisplayName("Crea un OrderItem válido con cantidad positiva y precio no negativo")
    void shouldCreateValidOrderItem() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");

        OrderItem item = new OrderItem(productId, 2, unitPrice);

        assertThat(item.getProductId()).isEqualTo(productId);
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualTo(unitPrice);
    }

    @Test
    @DisplayName("Lanza InvalidItemException cuando la cantidad es cero o negativa")
    void shouldRejectNonPositiveQuantity() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");

        assertThatThrownBy(() -> new OrderItem(productId, 0, unitPrice))
                .isInstanceOf(InvalidItemException.class);

        assertThatThrownBy(() -> new OrderItem(productId, -1, unitPrice))
                .isInstanceOf(InvalidItemException.class);
    }

    @Test
    @DisplayName("Lanza InvalidItemException cuando el precio unitario es negativo")
    void shouldRejectNegativeUnitPrice() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("-1.00"), "USD");

        assertThatThrownBy(() -> new OrderItem(productId, 1, unitPrice))
                .isInstanceOf(InvalidItemException.class);
    }

    @Test
    @DisplayName("Calcula correctamente el subtotal como unitPrice * quantity")
    void shouldCalculateSubTotal() {
        UUID productId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");

        OrderItem item = new OrderItem(productId, 3, unitPrice);

        Money subTotal = item.subTotal();

        assertThat(subTotal.getAmount()).isEqualByComparingTo("15.00");
        assertThat(subTotal.getCurrency()).isEqualTo("USD");
    }
}

