package com.example.order_management.infrastructure.persistence;

import com.example.order_management.domain.Money;
import com.example.order_management.domain.Order;
import com.example.order_management.domain.OrderItem;
import com.example.order_management.domain.OrderStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class OrderEntityMapper {

    static OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity(
                order.getOrderId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                order.getCreatedAt()
        );

        for (OrderItem item : order.getItems()) {
            OrderItemEntity itemEntity = new OrderItemEntity(
                    UUID.randomUUID(),
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice().getAmount()
            );
            entity.addItem(itemEntity);
        }

        return entity;
    }

    static Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(OrderEntityMapper::toDomainItem)
                .collect(Collectors.toList());

        Money totalAmount = Money.of(entity.getTotalAmount(), entity.getCurrency());

        return Order.restore(
                entity.getId(),
                entity.getCustomerId(),
                items,
                entity.getCreatedAt(),
                totalAmount,
                OrderStatus.valueOf(entity.getStatus())
        );
    }

    private static OrderItem toDomainItem(OrderItemEntity itemEntity) {
        Money unitPrice = Money.of(itemEntity.getUnitPrice());
        return new OrderItem(
                itemEntity.getProductId(),
                itemEntity.getQuantity(),
                unitPrice
        );
    }
}

