package com.foodecommerce.foodies.entity;

import com.foodecommerce.foodies.enums.OrderStatus;
import com.foodecommerce.foodies.enums.PaymentStatus;
import com.foodecommerce.foodies.io.OrderItem;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
public class OrderEntity {
    @Id
    private String id;
    private String userId;
    private String userAddress;
    private String userPhone;
    private String userEmail;
    private List<OrderItem> orderItems;
    private long amount;
    private PaymentStatus paymentStatus;
    private String paymentCode;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void markPaid(){
        this.paymentStatus = PaymentStatus.PAID;
        this.orderStatus = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }
}
