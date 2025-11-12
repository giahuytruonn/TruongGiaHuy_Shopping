package iuh.repository;

import iuh.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepostiory extends JpaRepository<Order, Integer> {

}
