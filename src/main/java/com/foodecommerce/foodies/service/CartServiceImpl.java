package com.foodecommerce.foodies.service;

import com.foodecommerce.foodies.entity.CartEntity;
import com.foodecommerce.foodies.io.CartRequest;
import com.foodecommerce.foodies.io.CartResponse;
import com.foodecommerce.foodies.repository.CartRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final  CartRepository cartRepository;
    private final  UserService userService;


    @Override
    public CartResponse addToCart(CartRequest request) {
        String loggedByUserId = userService.findByUserId();
        Optional<CartEntity> cartEntity = cartRepository.findByUserId(loggedByUserId);
        CartEntity cart =  cartEntity.orElseGet(() -> new CartEntity(loggedByUserId, new HashMap<>()));
        Map<String,Integer> cartItems = cart.getItems();
        cartItems.put(request.getFoodId(), cartItems.getOrDefault(request.getFoodId(), 0) + 1);
        cart.setItems(cartItems);
        cartRepository.save(cart);
        return convertToResponse(cart);
     }

    @Override
    public CartResponse getCart() {
        String loggedByUserId = userService.findByUserId();
        CartEntity entity = cartRepository.findByUserId(loggedByUserId)
                .orElse(new CartEntity(null, loggedByUserId, new HashMap<>()));
        return   convertToResponse(entity);
    }

    @Override
    public void clearCart() {
        String loggedByUserId = userService.findByUserId();
        cartRepository.deleteByUserId(loggedByUserId);
    }

    @Override
    public CartResponse removeFromCartDetail(CartRequest cartRequest) {
        String loggedByUserId = userService.findByUserId();
        cartRepository.findByUserId(loggedByUserId);
        CartEntity cartEntity = cartRepository.findByUserId(loggedByUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        Map<String, Integer> cartItems = cartEntity.getItems();
        if (cartItems.containsKey(cartRequest.getFoodId())) {
            int currentQuantity = cartItems.get(cartRequest.getFoodId());
            if (currentQuantity > 0){
                cartItems.put(cartRequest.getFoodId(), --currentQuantity);
            }else  {
                cartItems.remove(cartRequest.getFoodId());
            }
            cartRepository.save(cartEntity);
        }
        return convertToResponse(cartEntity);
    }

    @Override
    public void removeItemFromCart(CartRequest cartRequest) {
        String loggedByUserId = userService.findByUserId();
        cartRepository.findByUserId(loggedByUserId);
        CartEntity cartEntity = cartRepository.findByUserId(loggedByUserId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));
        Map<String, Integer> cartItems = cartEntity.getItems();
        if (cartItems.containsKey(cartRequest.getFoodId())) {
            cartItems.remove(cartRequest.getFoodId());
        }
        cartRepository.save(cartEntity);
    }

    private CartResponse convertToResponse (CartEntity cartEntity) {
        return CartResponse.builder()
                .id(cartEntity.getId())
                .userId(cartEntity.getUserId())
                .items(cartEntity.getItems())
                .build();
    }
}
