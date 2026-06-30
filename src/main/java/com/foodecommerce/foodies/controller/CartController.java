package com.foodecommerce.foodies.controller;

import com.foodecommerce.foodies.io.CartRequest;
import com.foodecommerce.foodies.io.CartResponse;
import com.foodecommerce.foodies.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartResponse addToCart(@RequestBody CartRequest request) {
        String foodId = request.getFoodId();
        if (foodId == null || foodId.isEmpty()) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Food Id is empty");
        }
        return cartService.addToCart(request);
    }

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCart();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clearCart() {
        cartService.clearCart();
    }

    //xóa quantities theo item
    @PostMapping("/remove")
    public CartResponse removeFromCartDetail(@RequestBody CartRequest request) {

        String foodId = request.getFoodId();
        if (foodId == null || foodId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy Food ID");
        }
        return cartService.removeFromCartDetail(request);
    }

    @PostMapping("/removeItem")
    public void removeItemFromCart(@RequestBody CartRequest request) {

        String foodId = request.getFoodId();
        if (foodId == null || foodId.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không tìm thấy Food ID");
        }
         cartService.removeItemFromCart(request);
    }
}
