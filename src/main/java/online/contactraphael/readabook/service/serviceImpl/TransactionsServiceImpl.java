package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.payment.PaymentStatus;
import online.contactraphael.readabook.model.payment.Transactions;
import online.contactraphael.readabook.repository.TransactionsRepository;
import online.contactraphael.readabook.service.service.TransactionsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionsServiceImpl implements TransactionsService {

    private final TransactionsRepository transactionsRepository;

    @Override
    public void newPayment(
            String bookId,
            String userEmail,
            String paymentReference,
            String transactionReference,
            double amountPaid,
            String paymentMethod,
            String paymentStatus,
            String purpose) {

        Optional<Transactions> transactionsOptional = checkPayment(bookId);

        if(transactionsOptional.isPresent()) {

            Transactions oldPayment = transactionsOptional.get();

            if(!oldPayment.getPaymentStatus().equals(PaymentStatus.PAID)) {
                oldPayment.setAmountPaid(amountPaid);
                oldPayment.setPaymentMethod(paymentMethod);
                oldPayment.setTransactionReference(transactionReference);
                oldPayment.setPaymentReference(paymentReference);
                oldPayment.setPaymentStatus(PaymentStatus.valueOf(paymentStatus));
            }
            else throw new InvalidRequestParamException("Payment for book with id " + bookId + "already made");
        }
        else {

            Transactions transactions = Transactions.builder()
                    .transactionReference(transactionReference)
                    .paymentReference(paymentReference)
                    .amountPaid(amountPaid)
                    .createdAt(Instant.now())
                    .paymentMethod(paymentMethod)
                    .ownerEmail(userEmail)
                    .purpose(purpose)
                    .paymentStatus(PaymentStatus.valueOf(paymentStatus))
                    .bookId(bookId)
                    .build();

            transactionsRepository.save(transactions);
        }

    }

    @Override
    public Transactions findByReference(String paymentReference) {
        return
                transactionsRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid payment reference " + paymentReference));
    }

    @Override
    public Transactions findByTransactionReference(String transactionReference) {
        return
                transactionsRepository.findByTransactionReference(transactionReference)
                        .orElseThrow(() -> new ResourceNotFoundException("Invalid payment reference " + transactionReference));
    }

    private Optional<Transactions> checkPayment(String bookId) {
        return transactionsRepository.findByBookId(bookId);
    }
}
