package com.la.javaweb.service;

import com.la.javaweb.model.Product;
import com.la.javaweb.util.exception.AppException;

import java.util.Set;

public interface IWishListService {
    void addToWishList(Long userId, Long productId) throws AppException;
    Set<Product> getWishListByUserId(Long userId) throws AppException;
    void removeFromWishList(Long wishListId);
}
