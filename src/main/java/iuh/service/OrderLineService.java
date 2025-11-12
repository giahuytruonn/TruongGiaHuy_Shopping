package iuh.service;

import iuh.model.OrderLine;
import iuh.model.OrderLineId;
import iuh.repository.OrderLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderLineService {

    @Autowired
    private OrderLineRepository repository;

    public OrderLine save(OrderLine orderLine) {
        return repository.save(orderLine);
    }

    public OrderLine findById(OrderLineId id) {
        return repository.findById(id).orElse(null);
    }
}


