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


}
