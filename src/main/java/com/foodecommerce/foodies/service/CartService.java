package com.foodecommerce.foodies.service;

import com.foodecommerce.foodies.entity.CartEntity;
import com.foodecommerce.foodies.io.CartRequest;
import com.foodecommerce.foodies.io.CartResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartService {

    CartResponse addToCart(CartRequest cartRequest);

    CartResponse getCart();

    void clearCart();

    //delete quantity của mỗi item trong giỏ hàng
    CartResponse removeFromCartDetail(CartRequest cartRequest);

    void removeItemFromCart(CartRequest cartRequest);
}
