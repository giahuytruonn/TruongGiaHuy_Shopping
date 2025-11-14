package iuh.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderLine {

    @EmbeddedId
    private OrderLineId id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be > 0")
    private Integer amount;

    @NotNull(message = "Purchase price is required")
    private Double purchasePrice;
}
