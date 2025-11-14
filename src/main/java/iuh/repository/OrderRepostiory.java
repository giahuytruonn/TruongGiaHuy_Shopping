package iuh.repository;

import iuh.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepostiory extends JpaRepository<Order, Integer> {
    
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN o.customer c LEFT JOIN o.orderLines ol LEFT JOIN ol.product p WHERE " +
           "CAST(o.id AS string) LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Order> search(@Param("keyword") String keyword);
}
