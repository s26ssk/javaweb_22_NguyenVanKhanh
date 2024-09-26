package com.la.javaweb.service;

import com.la.javaweb.dto.request.CartItemRequest;
import com.la.javaweb.dto.response.CartItemResponse;
import com.la.javaweb.util.exception.AppException;

import java.util.List;

public interface IShoppingCartService {
    void addProductToCart(String username, CartItemRequest cartItemRequest) throws AppException;
    List<CartItemResponse> getCartItems(String username) throws AppException;
    void updateCartItemQuantity(Long shoppingCartId, CartItemRequest cartItemRequest) throws AppException;
    void removeProductFromCart(Long shoppingCartId) throws AppException;
    void removeAllProductsFromCart(String username);
    void clearCart(String username);
}
