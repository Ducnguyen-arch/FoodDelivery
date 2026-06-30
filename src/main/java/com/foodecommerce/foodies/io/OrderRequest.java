package com.foodecommerce.foodies.io;
import com.foodecommerce.foodies.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private List<OrderItem> orderItems;
    private String userAddress;
    private long amount;
    private String email;
    private String userPhoneNumber;
    private OrderStatus orderStatus;
    private String paymentCode;
}
