package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "SELECT book FROM Book book WHERE book.bookCode = ?1")
    Optional<Book> findByBookCode(String bookCode);
}
