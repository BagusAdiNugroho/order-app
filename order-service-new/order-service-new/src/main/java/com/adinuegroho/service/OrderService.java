package com.adinuegroho.service;

import com.adinuegroho.dto.Customer;
import com.adinuegroho.dto.OrderLineResponse;
import com.adinuegroho.dto.OrderResponse;
import com.adinuegroho.dto.Product;
import com.adinuegroho.entity.Order;
import com.adinuegroho.entity.OrderLine;
import com.adinuegroho.repository.OrderRepository;
import com.adinuegroho.webclient.CustomerClient;
import com.adinuegroho.webclient.ProductClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Optional;

@Transactional
@Service
public class OrderService {

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private ProductClient productClient;

    @Autowired
    private OrderRepository orderRepository;

    public Order save(Order order) {
        for (OrderLine orderLine : order.getOrderLines()) {
            orderLine.setOrder(order);
        }
        return orderRepository.save(order);
    }

    @CircuitBreaker(name = "customerService", fallbackMethod = "fallbackFindCustomerById")
    public OrderResponse findById(Long id) {
        Optional<Order> optOrder = orderRepository.findById(id);
        if (!optOrder.isPresent()) {
            return null;
        }

        Order order = optOrder.get();
        OrderResponse response = new OrderResponse(order.getId(), order.getOrderNumber(), order.getOrderDate(),
                customerClient.findById(order.getCustomerId()), new ArrayList<OrderLineResponse>());

        for (OrderLine orderLine : order.getOrderLines()) {
            Product product = productClient.findById(orderLine.getProductId());
            response.getOrderLines().add(new OrderLineResponse(orderLine.getId(),
                    product, orderLine.getQuantity(),
                    orderLine.getPrice()));
        }
        return response;
    }

    private OrderResponse fallbackFindCustomerById(Long id, Throwable throwable) {
        return new OrderResponse();
    }

    public OrderResponse findByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse(order.getId(), order.getOrderNumber(), order.getOrderDate(),
                customerClient.findById(order.getCustomerId()), new ArrayList<OrderLineResponse>());

        for (OrderLine orderLine : order.getOrderLines()) {
            Product product = productClient.findById(orderLine.getProductId());
            response.getOrderLines().add(new OrderLineResponse(orderLine.getId(), product, orderLine.getQuantity(),
                    orderLine.getPrice()));
        }
        return response;
    }
}
