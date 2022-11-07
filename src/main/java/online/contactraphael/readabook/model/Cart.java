package online.contactraphael.readabook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.Instant;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @SequenceGenerator(name = "id_generator", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "id_generator")
    private Long id;
    private String userAddress;
    private Double totalAmount;

    @ManyToMany(fetch = EAGER)
    private Set<CartBook> books;
    private Instant createdAt;

}
