package com.foodecommerce.foodies.service;


import com.foodecommerce.foodies.enums.OrderStatus;
import com.foodecommerce.foodies.io.OrderRequest;
import com.foodecommerce.foodies.io.OrderResponse;
import com.foodecommerce.foodies.io.SepayWebhookRequest;

import java.util.List;
import java.util.Map;

public interface OrderService {

    OrderResponse createOrderWithPayment(OrderRequest orderRequest, String userEmail);

    void processWebhook(SepayWebhookRequest webhookRequest, String apiKey);

    List<OrderResponse> getUserOrders();

    void removeOrderById(String orderId);

    List<OrderResponse> getOrdersAllUsers();

    void updateOrderStatus(String orderId, OrderStatus status);
}
