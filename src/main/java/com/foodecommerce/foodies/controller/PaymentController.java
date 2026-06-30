package com.foodecommerce.foodies.controller;

import com.foodecommerce.foodies.io.SepayWebhookRequest;
import com.foodecommerce.foodies.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/webhook")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final OrderService orderService;

    //Sepay call khi có transaction khớp với account number
    //header: "apikey" là secret key trong Sepay Dashboard

    @PostMapping("/sepay")
    public ResponseEntity<Map<String, Boolean>> sepayWebhook(@RequestBody SepayWebhookRequest request,
                                                            @RequestHeader(value = "Authorization", required = false)String authorization) {

        log.info("[WEBHOOK] Raw Authorization header: '{}'", authorization);

        if (authorization == null || authorization.isBlank()) {
            log.warn("[WEBHOOK] Không có Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//                    .body(Map.of("error", "Missing Authorization header"));
        }

        // SePay gửi "Apikey <secret>" — trim để an toàn với whitespace thừa
        String apiKey = authorization.startsWith("Apikey ")
                ? authorization.substring("Apikey ".length()).trim()
                : authorization.trim();

        log.info("[WEBHOOK] Parsed apiKey: '{}'", apiKey);

        orderService.processWebhook(request, apiKey);
        return ResponseEntity.ok(Map.of("success", true));

      /*  if (authorization == null || !authorization.startsWith("Apikey ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Missing or invalid Authorization header"));
        }

        String apiKey = authorization.substring("Apikey ".length()).trim();
        orderService.processWebhook(request, apiKey);
        return ResponseEntity.ok(Map.of("thành công", "true"));*/
    }

}
