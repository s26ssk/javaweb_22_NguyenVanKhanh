package com.la.javaweb.repository;

import com.la.javaweb.model.Order;
import com.la.javaweb.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserUsernameOrderByCreatedAtDesc(String username);
    List<Order> findByUserUsernameAndStatusOrderByCreatedAtDesc(String username, OrderStatus status);
    Optional<Order> findByOrderIdAndStatus(Long orderId, OrderStatus status);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Optional<Order> findByOrderId(Long orderId);

    List<Order> findAllByUserUsernameAndStatus(String username,OrderStatus status);
}
