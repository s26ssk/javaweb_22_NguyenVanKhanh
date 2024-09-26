package com.la.javaweb.service;

import com.la.javaweb.model.Order;
import com.la.javaweb.model.OrderStatus;
import com.la.javaweb.util.exception.AppException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IOrderService {
    void placeOrder(Order order) throws AppException;
    List<Order> getOrdersByUsernameOrderByCreatedAtDesc(String username);
    List<Order> getOrdersByUsernameAndStatusOrderByCreatedAtDesc(String username, OrderStatus status);

    Optional<Order> cancelOrder(Long orderId);
    Page<Order> getAllOrdersWithPaginationAndSorting(Pageable pageable);
    Page<Order> getOrdersByStatusWithPaginationAndSorting(OrderStatus status, Pageable pageable);
    Optional<Order> getOrderById(Long orderId);
    void updateOrderStatus(Long orderId, OrderStatus newStatus) throws AppException;

    List<Order> getAllOrderByUserAndStatus(String username,OrderStatus orderStatus);

}
