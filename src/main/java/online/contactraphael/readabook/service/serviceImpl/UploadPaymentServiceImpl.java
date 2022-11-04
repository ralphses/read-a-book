package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.payment.PaymentStatus;
import online.contactraphael.readabook.model.payment.Transactions;
import online.contactraphael.readabook.repository.UploadPaymentRepository;
import online.contactraphael.readabook.service.service.UploadPaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UploadPaymentServiceImpl implements UploadPaymentService {

    private final UploadPaymentRepository uploadPaymentRepository;

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

        Optional<Transactions> uploadFeePaymentOptional = checkPayment(bookId);

        if(uploadFeePaymentOptional.isPresent()) {

            Transactions oldPayment = uploadFeePaymentOptional.get();

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

            Transactions newUploadPayment = Transactions.builder()
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

            uploadPaymentRepository.save(newUploadPayment);
        }

    }

    @Override
    public void updatePaymentStatus(String paymentReference, String paymentStatus) {

        Transactions transactions = uploadPaymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found " + paymentReference));

        if(!transactions.getPaymentStatus().name().equalsIgnoreCase("SUCCESS"))
            transactions.setPaymentStatus(PaymentStatus.valueOf(paymentStatus));
    }

    @Override
    public Transactions findByReference(String paymentReference) {
        return
                uploadPaymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid payment reference " + paymentReference));
    }

    @Override
    public Transactions findByTransactionReference(String transactionReference) {
        return
                uploadPaymentRepository.findByTransactionReference(transactionReference)
                        .orElseThrow(() -> new ResourceNotFoundException("Invalid payment reference " + transactionReference));
    }

    private Optional<Transactions> checkPayment(String bookId) {
        return uploadPaymentRepository.findByBookId(bookId);
    }
}