package iuh.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username is required")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Z][a-zA-Z]+(\\s+[A-Z][a-zA-Z]+)+$", 
             message = "Name must have at least two words, each starting uppercase")
    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Calendar customerSince;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}
