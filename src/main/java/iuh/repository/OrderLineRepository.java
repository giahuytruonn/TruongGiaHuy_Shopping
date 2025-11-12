package iuh.repository;

import iuh.model.OrderLine;
import iuh.model.OrderLineId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, OrderLineId> {

}


