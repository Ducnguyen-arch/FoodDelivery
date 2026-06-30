package com.foodecommerce.foodies.controller;

import com.foodecommerce.foodies.entity.OrderEntity;
import com.foodecommerce.foodies.enums.OrderStatus;
import com.foodecommerce.foodies.io.OrderRequest;
import com.foodecommerce.foodies.io.OrderResponse;
import com.foodecommerce.foodies.repository.OrderRepository;
import com.foodecommerce.foodies.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest orderRequest,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        return orderService.createOrderWithPayment(orderRequest,userDetails.getUsername());
    }

//    @GetMapping("/{id}/status")
//    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable String id) {
//        OrderEntity orderEntity = orderRepository.findById(id).orElseThrow(
//                () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        return ResponseEntity.ok(Map.of("status", orderEntity.getOrderStatus().name()));
//    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable String orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        return ResponseEntity.ok(Map.of(
                "orderId", order.getId(),
                "orderStatus", order.getOrderStatus().name(),
                "paymentStatus", order.getPaymentStatus().name()
        ));
    }


/*    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrderWithPayment(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrderWithPayment(orderRequest);
    }*/

    @GetMapping()
    public List<OrderResponse> getUserOrders() {
        return orderService.getUserOrders();
    }

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrderById(@PathVariable String orderId) {
        orderService.removeOrderById(orderId);
    }

    //admin
    @GetMapping("/all-orders")
    public List<OrderResponse> getOrdersAllUsers() {
        return orderService.getOrdersAllUsers();
    }

    //admin
    @PatchMapping("/status/{orderId}")
    public void updateOrderStatus(@PathVariable String orderId,
                                  @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
    }
}
