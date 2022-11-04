package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.Cart;
import online.contactraphael.readabook.model.dtos.monnify.NewPaymentRequest;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import online.contactraphael.readabook.utility.monnify.webhook.NewPaymentNotificationRequest;

public interface PaymentService {
    InitPaymentResponse initPayment(NewPaymentRequest newPaymentRequest);
    InitPaymentResponse initPayment(Cart cart, String email);

    void updatePayment(NewPaymentNotificationRequest newPaymentNotificationRequest);

    void confirmPayment(String paymentReference);
}
