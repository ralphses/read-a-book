package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.payment.Transactions;

public interface TransactionsService {

    void newPayment(
            String bookId,
            String userEmail,
            String paymentReference,
            String transactionReference,
            double amountPaid,
            String paymentMethod,
            String paymentStatus,
            String purpose);

    void updatePaymentStatus(String paymentReference, String paymentStatus);
    Transactions findByReference(String paymentReference);

    Transactions findByTransactionReference(String transactionReference);
}
