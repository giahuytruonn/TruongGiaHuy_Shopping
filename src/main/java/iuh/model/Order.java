package iuh.model;

import iuh.model.Customer;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Temporal(TemporalType.DATE)
    @PastOrPresent(message = "Order date cannot be in the future")
    private Calendar date;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderLine> orderLines = new HashSet<>();

    @Column(name = "payment_status")
    private String paymentStatus = "PENDING"; // PENDING, PAID, FAILED, CANCELLED

    @Column(name = "payos_order_code")
    private Long payosOrderCode; // Lưu orderCode từ PayOS

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount; // Tổng tiền đơn hàng
}
