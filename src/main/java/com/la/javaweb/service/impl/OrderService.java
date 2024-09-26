package com.la.javaweb.service.impl;

import com.la.javaweb.model.*;
import com.la.javaweb.repository.IOrderRepository;
import com.la.javaweb.repository.IProductRepository;
import com.la.javaweb.service.IOrderService;
import com.la.javaweb.service.IProductService;
import com.la.javaweb.util.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements IOrderService {
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private IProductService productService;

    @Override
    public void placeOrder(Order order) throws AppException{
        for (OrderDetails orderDetails : order.getOrderDetails()) {
            Product product = productRepository.findByProductId(orderDetails.getProduct().getProductId())
                    .orElseThrow(() -> new AppException("Product not found"));

            if (product.getStockQuantity() < orderDetails.getOrderQuantity()) {
                throw new AppException("Not enough stock for product with name: " + product.getProductName());
            }

            product.setStockQuantity(product.getStockQuantity() - orderDetails.getOrderQuantity());
            productRepository.save(product);
        }
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUsernameOrderByCreatedAtDesc(String username) {
        return orderRepository.findByUserUsernameOrderByCreatedAtDesc(username);
    }

    @Override
    public List<Order> getOrdersByUsernameAndStatusOrderByCreatedAtDesc(String username, OrderStatus status) {
        return orderRepository.findByUserUsernameAndStatusOrderByCreatedAtDesc(username, status);
    }

    @Override
    public Optional<Order> cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findByOrderIdAndStatus(orderId, OrderStatus.WAITING);
        return optionalOrder.map(order -> {
            order.setStatus(OrderStatus.CANCEL);
            order.getOrderDetails().forEach(e -> {
                int updateStock = e.getProduct().getStockQuantity() + e.getOrderQuantity();
                Optional<Product> updateProduct = productService.getProductById(e.getProduct().getProductId());
                Product update = updateProduct.get();
                update.setStockQuantity(updateStock);
                productRepository.save(update);
            });
            return orderRepository.save(order);
        });

    }

    @Override
    public Page<Order> getAllOrdersWithPaginationAndSorting(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    @Override
    public Page<Order> getOrdersByStatusWithPaginationAndSorting(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) throws AppException{
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            OrderStatus currentStatus = order.getStatus();

            if (currentStatus == OrderStatus.WAITING) {
                if (newStatus == OrderStatus.CONFIRM || newStatus == OrderStatus.DENIED) {
                    order.setStatus(newStatus);
                    orderRepository.save(order);
                }else{
                    throw new AppException("Unable to update order status");
                }
            } else if (currentStatus == OrderStatus.CONFIRM) {
                if (newStatus == OrderStatus.DELIVERY || newStatus == OrderStatus.DENIED) {
                    order.setStatus(newStatus);
                    orderRepository.save(order);
                }else{
                    throw new AppException("Unable to update order status");
                }
            } else if (currentStatus == OrderStatus.DELIVERY) {
                if (newStatus == OrderStatus.SUCCESS || newStatus == OrderStatus.DENIED) {
                    order.setStatus(newStatus);

                    orderRepository.save(order);
                }else{
                    throw new AppException("Unable to update order status");
                }
            }else {
                throw new AppException("Unable to update order status");
            }
        }
    }


    @Override
    public List<Order> getAllOrderByUserAndStatus(String username, OrderStatus orderStatus) {
        return orderRepository.findAllByUserUsernameAndStatus(username,orderStatus);
    }
}
