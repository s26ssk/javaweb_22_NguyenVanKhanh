package com.la.javaweb.service.impl;

import com.la.javaweb.model.OrderDetails;
import com.la.javaweb.repository.IOrderDetailsRepository;
import com.la.javaweb.service.IOrderDetailsService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDetailsService implements IOrderDetailsService {
    @Autowired
    private IOrderDetailsRepository orderDetailsRepository;

    @Override
    @Transactional
    public void saveAll(List<OrderDetails> orderDetailsList) {
        orderDetailsRepository.saveAll(orderDetailsList);
    }
}
