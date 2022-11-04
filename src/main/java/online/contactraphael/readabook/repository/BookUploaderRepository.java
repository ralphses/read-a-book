package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.book.BookUploader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookUploaderRepository extends JpaRepository<BookUploader, Long> {

    @Query(value = "SELECT uploader FROM BookUploader uploader WHERE uploader.email = ?1")
    Optional<BookUploader> findByEmail(String email);
}
