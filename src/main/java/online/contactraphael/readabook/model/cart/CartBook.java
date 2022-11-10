package online.contactraphael.readabook.model.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.contactraphael.readabook.model.book.Book;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartBook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer quantity;

    @OneToOne(fetch = FetchType.EAGER)
    private Book book;
}
