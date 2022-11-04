package online.contactraphael.readabook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.contactraphael.readabook.model.book.Book;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

import static javax.persistence.FetchType.EAGER;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(fetch = EAGER)
    private Set<Book> books;

    private String userAddress;
    private Instant createdAt;
}
