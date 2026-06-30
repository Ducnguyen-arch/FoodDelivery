package com.foodecommerce.foodies.service;

import com.foodecommerce.foodies.entity.OrderEntity;
import com.foodecommerce.foodies.enums.OrderStatus;
import com.foodecommerce.foodies.enums.PaymentStatus;
import com.foodecommerce.foodies.io.OrderRequest;
import com.foodecommerce.foodies.io.OrderResponse;
import com.foodecommerce.foodies.io.SepayWebhookRequest;
import com.foodecommerce.foodies.repository.CartRepository;
import com.foodecommerce.foodies.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartRepository cartRepository;

    @Value("${sepay.bank.account}")
    private String SEPAY_BANK_ACCOUNT;

    @Value("${sepay.bank.name}")
    private String SEPAY_BANK_NAME;


    @Value("${sepay.webhook.secret}")
    private String SEPAY_WEBHOOK_SECRET;


    @Override
    public OrderResponse createOrderWithPayment(OrderRequest orderRequest, String userEmail) {
        String paymentCode = "DN" + System.currentTimeMillis(); // mã duy nhất theo đơn hàng

        String userId = userService.findUserIdByEmail(userEmail); // cần có method này


        OrderEntity newOrder = convertToEntity(orderRequest);
        newOrder.setPaymentCode(paymentCode);
        newOrder.setUserId(userId);
        orderRepository.save(newOrder);

        String qrUrl = buildQRUrl(SEPAY_BANK_ACCOUNT, SEPAY_BANK_NAME, orderRequest.getAmount(), paymentCode);
        return  OrderResponse.builder()
                .id(newOrder.getId())
                .paymentCode(paymentCode)
                .qrUrl(qrUrl)
                .orderStatus(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .build();
    }

    @Override
    public void processWebhook(SepayWebhookRequest webhookRequest, String apiKey) {

        //verify API Key với Sepay - headers
        if (!SEPAY_WEBHOOK_SECRET.equals(apiKey)) {
            log.warn("Webhook rejected - invalid API Key");
            throw new SecurityException("API Key không hợp lệ");
        }

        String rawContent = webhookRequest.content();
        if (rawContent == null || rawContent.isBlank()) {
            log.warn("Webhook bỏ qua - Content empty");
            return;
        }
//        String paymentCode = webhookRequest.content();
//        if (paymentCode == null || paymentCode.isBlank()) {
//            log.warn("Webhook bỏ qua - Không có payment code trong content: {}", webhookRequest.content());
//            return;
//        }

        // paymentCode luôn là token đầu tiên, format: "DN<timestamp> <nội dung khác>"
        String paymentCode = rawContent.trim().split("\\s+")[0];

        OrderEntity orderEntity = orderRepository.findByPaymentCode(paymentCode)
                .orElseThrow(() -> {
                    log.error("Không tìm thấy order, paymentCode={}", paymentCode);
                    return new RuntimeException("Không tìm thấy order, paymentCode={}" + paymentCode);
                });

        if (orderEntity.getPaymentStatus() == PaymentStatus.PAID) {
            log.info("Webhook idempotent with order {} PAID", orderEntity.getId());
            return;
        }

        if (webhookRequest.transferAmount() < orderEntity.getAmount()) {
            log.warn("Webhook: Số tiền đang chuyển {} nhỏ hơn số tiền {} của đơn hàng {}",
                    webhookRequest.transferAmount(), orderEntity.getAmount(), orderEntity.getPaymentCode());
            throw new RuntimeException("Chuyển không đủ tiền. Vui lòng thử lại");
        }

        orderEntity.markPaid();
        orderRepository.save(orderEntity);
        cartRepository.deleteByUserId(orderEntity.getUserId());

        log.info("Order {} confirmed by Sepay Webhook, paymentCode={}", orderEntity.getId(), paymentCode);

    }

    private String buildQRUrl(String acc, String bank, long amount, String des) {
        return String.format(
                "https://qr.sepay.vn/img?bank=%s&acc=%s&amount=%d&des=%s",
                bank,
                acc,
                amount,
                des
        );
    }
    

    //get order by userId
    @Override
    public List<OrderResponse> getUserOrders() {
        String loggedInUserId = userService.findByUserId();
        List<OrderEntity> listOrders = orderRepository.findByUserId(loggedInUserId);
        return listOrders.stream().map(this::convertToResponse).toList();
    }

    @Override
    public void removeOrderById(String orderId) {
        orderRepository.deleteById(orderId);
    }

    //for admin
    @Override
    public List<OrderResponse> getOrdersAllUsers() {
        List<OrderEntity> listOrders = orderRepository.findAll();
        return listOrders.stream().map(this::convertToResponse).toList();
    }

    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order with id " + orderId + " not found"));
        orderEntity.setOrderStatus(status);
        orderRepository.save(orderEntity);
    }


    private OrderResponse convertToResponse(OrderEntity newOrder) {
        return OrderResponse.builder()
                .id(newOrder.getId())
                .amount(newOrder.getAmount())
                .userAddress(newOrder.getUserAddress())
                .userId(newOrder.getUserId())
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PENDING)
                .userEmail(newOrder.getUserEmail())
                .userPhone(newOrder.getUserPhone())
                .orderItems(newOrder.getOrderItems())
                .build();
    }


    private OrderEntity convertToEntity(OrderRequest orderRequest) {
        return OrderEntity.builder()
                .userEmail(orderRequest.getEmail())
                .amount(orderRequest.getAmount())
                .userAddress(orderRequest.getUserAddress())
                .orderItems(orderRequest.getOrderItems())
                .userPhone(orderRequest.getUserPhoneNumber())
                .paymentStatus(PaymentStatus.PENDING)
                .orderStatus(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
