package iuh.service;

import iuh.model.Order;
import iuh.repository.OrderRepostiory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepostiory repository;

    public List<Order> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Integer id) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            Order o = order.get();
            // Force load orderLines to avoid lazy loading issues
            o.getOrderLines().size();
            return o;
        }
        return null;
    }

    public Order save(Order order) {
        return repository.save(order);
    }

    public Order findByPayosOrderCode(Long payosOrderCode) {
        return repository.findAll().stream()
                .filter(order -> order.getPayosOrderCode() != null && 
                        order.getPayosOrderCode().equals(payosOrderCode))
                .findFirst()
                .orElse(null);
    }

    @Transactional
    public void updatePaymentStatus(Integer orderId, String status) {
        Order order = findById(orderId);
        if (order != null) {
            order.setPaymentStatus(status);
            repository.save(order);
        }
    }

    @Transactional
    public void updatePaymentStatusByPayosCode(Long payosOrderCode, String status) {
        Order order = findByPayosOrderCode(payosOrderCode);
        if (order != null) {
            order.setPaymentStatus(status);
            repository.save(order);
        }
    }

}
