package com.la.javaweb.service.impl;

import com.la.javaweb.dto.request.CartItemRequest;
import com.la.javaweb.dto.response.CartItemResponse;
import com.la.javaweb.model.Product;
import com.la.javaweb.model.ShoppingCart;
import com.la.javaweb.model.Users;
import com.la.javaweb.repository.IProductRepository;
import com.la.javaweb.repository.IShoppingCartRepository;
import com.la.javaweb.repository.IUserRepository;
import com.la.javaweb.service.IShoppingCartService;
import com.la.javaweb.util.exception.AppException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService implements IShoppingCartService {
    @Autowired
    private IShoppingCartRepository shoppingCartRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IProductRepository productRepository;

    @Override
    public void addProductToCart(String username, CartItemRequest cartItemRequest) throws AppException{
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("User not found"));

        Product product = productRepository.findByProductId(cartItemRequest.getProductId())
                .orElseThrow(() -> new AppException("Product not found"));

        Optional<ShoppingCart> existingCartItem = shoppingCartRepository.findByUserAndProduct(user, product);

        ShoppingCart cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setOrderQuantity(cartItem.getOrderQuantity() + cartItemRequest.getQuantity());
        } else {
            cartItem = new ShoppingCart();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setOrderQuantity(cartItemRequest.getQuantity());
        }

        shoppingCartRepository.save(cartItem);
    }

    @Override
    public List<CartItemResponse> getCartItems(String username) throws AppException{
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException("User not found"));

        List<ShoppingCart> cartItems = shoppingCartRepository.findByUser(user);

        return cartItems.stream()
                .map(cartItem -> CartItemResponse.builder()
                        .shoppingCartId(cartItem.getShoppingCartId())
                        .productId(cartItem.getProduct().getProductId())
                        .productName(cartItem.getProduct().getProductName())
                        .quantity(cartItem.getOrderQuantity())
                        .images(cartItem.getProduct().getImages())
                        .totalPriceProduct(cartItem.getProduct().getExportPrice()*cartItem.getOrderQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void updateCartItemQuantity(Long cartItemId, CartItemRequest cartItemRequest) throws AppException {
        ShoppingCart cartItem = shoppingCartRepository.findById(cartItemId)
                .orElseThrow(() -> new AppException("Cart item not found"));

        int newQuantity = cartItemRequest.getQuantity();
        if (newQuantity == 0) {
            shoppingCartRepository.deleteById(cartItemId);
        } else if (newQuantity > 0) {
            cartItem.setOrderQuantity(newQuantity);
            shoppingCartRepository.save(cartItem);
        } else {
            throw new AppException("Invalid quantity. The quantity must be positive or 0");
        }
    }

    @Override
    public void removeProductFromCart(Long shoppingCartId) throws AppException{
        ShoppingCart cartItem = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new AppException("Cart item not found"));

        shoppingCartRepository.delete(cartItem);
    }

    @Override
    @Transactional
    public void removeAllProductsFromCart(String username) {
        shoppingCartRepository.deleteAllByUsername(username);
    }

    @Override
    @Transactional
    public void clearCart(String username) {
        shoppingCartRepository.deleteAllByUsername(username);
    }
}
