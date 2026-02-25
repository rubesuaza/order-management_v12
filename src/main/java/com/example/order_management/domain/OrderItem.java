package com.example.order_management.domain;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrderItem {

    private final UUID productId;
    private final int quantity;
    private final Money unitPrice;

    public OrderItem(UUID productId, int quantity, Money unitPrice) {
        if (productId == null) {
            throw new InvalidItemException("ProductId must not be null");
        }
        if (quantity <= 0) {
            throw new InvalidItemException("Quantity must be greater than zero");
        }
        if (unitPrice == null) {
            throw new InvalidItemException("Unit price must not be null");
        }
        if (unitPrice.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidItemException("Unit price must not be negative");
        }

        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public Money getUnitPrice() {
        return unitPrice;
    }

    public Money subTotal() {
        return unitPrice.multiply(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem that)) return false;
        return quantity == that.quantity &&
                Objects.equals(productId, that.productId) &&
                Objects.equals(unitPrice, that.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, quantity, unitPrice);
    }
}

