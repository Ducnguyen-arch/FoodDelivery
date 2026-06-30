package com.foodecommerce.foodies.io;

import com.foodecommerce.foodies.enums.OrderStatus;
import com.foodecommerce.foodies.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String id;
    private String userId;
    private String userAddress;
    private String userPhone;
    private String userEmail;
    private long amount;
    private PaymentStatus paymentStatus;
    private OrderStatus orderStatus;
    private List<OrderItem> orderItems;

    private String paymentCode;
    private String qrUrl;

}
