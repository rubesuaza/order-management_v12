package com.example.order_management.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    @DisplayName("No permite crear una orden vacía")
    void shouldNotAllowEmptyOrder() {
        UUID customerId = UUID.randomUUID();

        assertThatThrownBy(() -> Order.create(customerId, List.of()))
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    @DisplayName("Calcula correctamente el total a partir de los OrderItem")
    void shouldCalculateTotalAmountFromItems() {
        UUID customerId = UUID.randomUUID();
        Money unitPrice1 = Money.of(new BigDecimal("5.00"), "USD");
        Money unitPrice2 = Money.of(new BigDecimal("10.00"), "USD");

        OrderItem item1 = new OrderItem(UUID.randomUUID(), 2, unitPrice1); // 10
        OrderItem item2 = new OrderItem(UUID.randomUUID(), 1, unitPrice2); // 10

        Order order = Order.create(customerId, List.of(item1, item2));

        assertThat(order.getTotalAmount().getAmount()).isEqualByComparingTo("20.00");
        assertThat(order.getTotalAmount().getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("No permite marcar como pagada una orden con total menor a 10 USD")
    void shouldNotAllowPayWhenTotalLessThanMinimum() {
        UUID customerId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("4.00"), "USD");

        OrderItem item = new OrderItem(UUID.randomUUID(), 2, unitPrice); // 8

        Order order = Order.create(customerId, List.of(item));

        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    @DisplayName("Permite marcar como pagada una orden con total mayor o igual al mínimo")
    void shouldAllowPayWhenTotalAtLeastMinimum() {
        UUID customerId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");

        OrderItem item = new OrderItem(UUID.randomUUID(), 2, unitPrice); // 10

        Order order = Order.create(customerId, List.of(item));

        order.markAsPaid();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("Solo permite cancelar órdenes en estado PENDING o PAID")
    void shouldOnlyAllowCancelFromPendingOrPaid() {
        UUID customerId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, unitPrice);

        Order pendingOrder = Order.create(customerId, List.of(item));
        pendingOrder.cancel();
        assertThat(pendingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Order paidOrder = Order.create(customerId, List.of(item));
        paidOrder.markAsPaid();
        paidOrder.cancel();
        assertThat(paidOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        Order shippedOrder = Order.create(customerId, List.of(item));
        shippedOrder.markAsPaid();
        shippedOrder.ship();

        assertThatThrownBy(shippedOrder::cancel)
                .isInstanceOf(InvalidOrderStateException.class);
    }

    @Test
    @DisplayName("Solo permite enviar órdenes en estado PAID")
    void shouldOnlyAllowShipFromPaid() {
        UUID customerId = UUID.randomUUID();
        Money unitPrice = Money.of(new BigDecimal("5.00"), "USD");
        OrderItem item = new OrderItem(UUID.randomUUID(), 2, unitPrice);

        Order order = Order.create(customerId, List.of(item));

        assertThatThrownBy(order::ship)
                .isInstanceOf(InvalidOrderStateException.class);

        order.markAsPaid();
        order.ship();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }
}

