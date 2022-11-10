package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.payment.PaymentStatus;
import online.contactraphael.readabook.model.payment.Transactions;
import online.contactraphael.readabook.repository.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class TransactionsServiceImplTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @InjectMocks
    private TransactionsServiceImpl underTest;

    private Transactions transactions;

    @BeforeEach
    void setUp() {

        String bookId = "id";

        transactions = Transactions.builder()
                .transactionReference("transactionReference")
                .paymentReference("paymentReference")
                .amountPaid(30.0)
                .createdAt(Instant.now())
                .paymentMethod("TRANSFER")
                .ownerEmail("userEmail")
                .purpose("purpose")
                .paymentStatus(PaymentStatus.valueOf("PENDING"))
                .bookId(bookId)
                .build();

        Mockito.when(transactionsRepository.findByBookId(bookId))
                .thenReturn(Optional.of(transactions));
    }

    @Test
    public void itShouldInitiateNewTransactionWithValidParams() {
        underTest.newPayment("id", "userEmail", "paymentReference", "transactionReference",
                20.0, "TRANSFER", "PAID", "purpose");
    }

    @Test
    public void itShouldInitiateNewTransactionWithInValidParamsForAlreadyPaidTransaction() {
        transactions.setPaymentStatus(PaymentStatus.PAID);
        assertThrows(InvalidRequestParamException.class, () -> underTest.newPayment("id", "userEmail", "paymentReference", "transactionReference",
                20.0, "TRANSFER", "PAID", "purpose"));
    }

    @Test
    public void itShouldInitiateNewTransactionWithValidParamsForNewTransaction() {
        String bookId = "id";
        Mockito.when(transactionsRepository.findByBookId(bookId))
                .thenReturn(Optional.empty());
        underTest.newPayment("id", "userEmail", "paymentReference", "transactionReference",
                20.0, "TRANSFER", "PAID", "purpose");
    }

    @Test
    public void itShouldFindTransactionsByPaymentReference() {
        String paymentReference = "paymentReference";

        Mockito.when(transactionsRepository.findByPaymentReference(paymentReference))
                .thenReturn(Optional.of(transactions));

        underTest.findByReference(paymentReference);
    }

    @Test
    public void itShouldFindTransactionsByPaymentReferenceWithInvalidPaymentReference() {
        String paymentReference = "paymentReference";

        Mockito.when(transactionsRepository.findByPaymentReference(paymentReference))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->underTest.findByReference(paymentReference));
    }

    @Test
    public void itShouldFindTransactionsByTransactionReference() {
        String transactionRef = "paymentReference";

        Mockito.when(transactionsRepository.findByTransactionReference(transactionRef))
                .thenReturn(Optional.of(transactions));

        underTest.findByTransactionReference(transactionRef);
    }

    @Test
    public void itShouldFindTransactionsByPaymentReferenceWithInvalidTransactionReference() {
        String transactionRef = "paymentReference";

        Mockito.when(transactionsRepository.findByTransactionReference(transactionRef))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->underTest.findByTransactionReference(transactionRef));
    }
}