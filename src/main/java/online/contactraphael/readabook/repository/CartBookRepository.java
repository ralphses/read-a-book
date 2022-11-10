package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.cart.CartBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartBookRepository extends JpaRepository<CartBook, Long> {

    @Query(value = "SELECT cartBook FROM CartBook cartBook WHERE cartBook.book.bookCode = ?1")
    Optional<CartBook> findByBookCode(String bookCode);
}
