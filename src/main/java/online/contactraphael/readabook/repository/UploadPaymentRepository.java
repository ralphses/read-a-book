package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.payment.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UploadPaymentRepository extends JpaRepository<Transactions, Long> {

    @Query(value = "SELECT pay FROM Transactions pay WHERE pay.paymentReference = ?1")
    Optional<Transactions> findByPaymentReference(String paymentReference);

    @Query(value = "SELECT pay FROM Transactions pay WHERE pay.transactionReference = ?1")
    Optional<Transactions> findByTransactionReference(String transactionReference);

    @Query(value = "SELECT pay FROM Transactions pay WHERE pay.bookId = ?1")
    Optional<Transactions> findByBookId(String bookId);
}
