package com.la.javaweb.service;

import com.la.javaweb.model.OrderDetails;

import java.util.List;

public interface IOrderDetailsService {
    void saveAll(List<OrderDetails> orderDetailsList);
}
