package com.example.order_management.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {

    private static final BigDecimal MIN_ORDER_AMOUNT_USD = new BigDecimal("10.00");

    private final UUID orderId;
    private final UUID customerId;
    private final List<OrderItem> items;
    private final LocalDateTime createdAt;
    private Money totalAmount;
    private OrderStatus status;

    private Order(UUID customerId, List<OrderItem> items) {
        this.orderId = UUID.randomUUID();
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.createdAt = LocalDateTime.now();
        this.totalAmount = calculateTotal(items);
        this.status = OrderStatus.PENDING;
    }

    public static Order create(UUID customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderStateException("Order must contain at least one item");
        }
        return new Order(customerId, items);
    }

    private Money calculateTotal(List<OrderItem> items) {
        Money total = null;
        for (OrderItem item : items) {
            Money subTotal = item.subTotal();
            if (total == null) {
                total = subTotal;
            } else {
                total = total.add(subTotal);
            }
        }
        if (total == null) {
            return Money.of(BigDecimal.ZERO, "USD");
        }
        return total;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void markAsPaid() {
        if (status != OrderStatus.PENDING) {
            throw new InvalidOrderStateException("Only PENDING orders can be marked as PAID");
        }
        if (totalAmount.getAmount().compareTo(MIN_ORDER_AMOUNT_USD) < 0) {
            throw new InvalidOrderStateException("Order total must be at least 10.00 USD to be paid");
        }
        this.status = OrderStatus.PAID;
    }

    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new InvalidOrderStateException("Only PAID orders can be shipped");
        }
        this.status = OrderStatus.SHIPPED;
    }

    public void markAsDelivered() {
        if (status != OrderStatus.SHIPPED) {
            throw new InvalidOrderStateException("Only SHIPPED orders can be delivered");
        }
        this.status = OrderStatus.DELIVERED;
    }

    public void cancel() {
        if (status == OrderStatus.PENDING || status == OrderStatus.PAID) {
            this.status = OrderStatus.CANCELLED;
        } else {
            throw new InvalidOrderStateException("Only PENDING or PAID orders can be cancelled");
        }
    }
}

