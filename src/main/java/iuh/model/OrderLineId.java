package iuh.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Embeddable
public class OrderLineId implements Serializable {
    private Integer orderId;
    private Integer productId;

}