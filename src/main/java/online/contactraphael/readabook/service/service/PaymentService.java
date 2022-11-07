package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.dtos.monnify.NewMonnifyPaymentRequest;
import online.contactraphael.readabook.model.dtos.monnify.NewPaymentRequest;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import online.contactraphael.readabook.utility.monnify.webhook.NewMonnifyPaymentNotificationRequest;

public interface PaymentService {
    InitPaymentResponse initNewBookPayment(NewPaymentRequest newPaymentRequest);

    void updatePayment(NewMonnifyPaymentNotificationRequest newMonnifyPaymentNotificationRequest);

    String confirmPayment(String paymentReference);

    InitPaymentResponse initializeNewMonnifyPayment(NewMonnifyPaymentRequest newMonnifyPaymentRequest);
}
