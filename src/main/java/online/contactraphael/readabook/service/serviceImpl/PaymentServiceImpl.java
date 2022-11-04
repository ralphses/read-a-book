package online.contactraphael.readabook.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.model.Cart;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.book.BookStatus;
import online.contactraphael.readabook.model.dtos.monnify.NewMonnifyPaymentRequest;
import online.contactraphael.readabook.model.dtos.monnify.NewPaymentRequest;
import online.contactraphael.readabook.model.payment.PaymentStatus;
import online.contactraphael.readabook.model.payment.Transactions;
import online.contactraphael.readabook.service.service.BookService;
import online.contactraphael.readabook.service.service.PaymentService;
import online.contactraphael.readabook.service.service.UploadPaymentService;
import online.contactraphael.readabook.utility.CustomWebClient;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import online.contactraphael.readabook.utility.monnify.MonnifyConfig;
import online.contactraphael.readabook.utility.monnify.MonnifyCredential;
import online.contactraphael.readabook.utility.monnify.MonnifyResponse;
import online.contactraphael.readabook.utility.monnify.webhook.EventData;
import online.contactraphael.readabook.utility.monnify.webhook.NewPaymentNotificationRequest;
import online.contactraphael.readabook.utility.monnify.webhook.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static online.contactraphael.readabook.configuration.main.AppConfig.PAYMENT_REDIRECT_URL;
import static online.contactraphael.readabook.model.payment.PaymentStatus.PAID;
import static online.contactraphael.readabook.model.payment.PaymentStatus.PENDING;
import static online.contactraphael.readabook.utility.monnify.MonnifyConfig.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CustomWebClient webClient;
    private final MonnifyCredential monnifyCredential;
    private final UploadPaymentService uploadPaymentService;
    private final BookService bookService;

    @Override
    public InitPaymentResponse initPayment(NewPaymentRequest newPaymentRequest) {

        NewMonnifyPaymentRequest newMonnifyPaymentRequest = buildRequest(newPaymentRequest);

        MonnifyResponse response = (MonnifyResponse) webClient.sendRequest(
                MONNIFY_BASE_URL, INIT_TRANSACTION_URL, POST, newMonnifyPaymentRequest, getAuthorizationHeader(), APPLICATION_JSON, MonnifyResponse.class);

        InitPaymentResponse initPaymentResponse = new ObjectMapper().convertValue(response.getResponseBody(), InitPaymentResponse.class);

        //Save new uploadPaymentFee
        uploadPaymentService.newPayment(
                newPaymentRequest.bookId(),
                newPaymentRequest.userEmail(),
                initPaymentResponse.getPaymentReference(),
                initPaymentResponse.getTransactionReference(),
                newPaymentRequest.amount(),
                null,
                "PENDING",
                "Book upload fee");

        return initPaymentResponse;

    }

    @Override
    public InitPaymentResponse initPayment(Cart cart, String email) {

        NewMonnifyPaymentRequest newMonnifyPaymentRequest = NewMonnifyPaymentRequest.builder()
                .redirectUrl(PAYMENT_REDIRECT_URL)
                .customerName(email)
                .paymentReference(UUID.randomUUID().toString().replace("-", ""))
                .paymentMethods(ALLOWED_PAYMENT_METHODS)
                .paymentDescription("Book purchase")
                .customerEmail(email)
                .currencyCode(NGN_CURRENCY_CODE)
                .contractCode(CONTRACT_CODE)
                .amount(cart.getTotalAmount())
                .build();

        MonnifyResponse response = (MonnifyResponse) webClient.sendRequest(
                MONNIFY_BASE_URL, INIT_TRANSACTION_URL, POST, newMonnifyPaymentRequest, getAuthorizationHeader(), APPLICATION_JSON, MonnifyResponse.class);

        InitPaymentResponse initPaymentResponse = new ObjectMapper().convertValue(response.getResponseBody(), InitPaymentResponse.class);

        uploadPaymentService.newPayment(
                "",
                email,
                initPaymentResponse.getPaymentReference(),
                initPaymentResponse.getTransactionReference(),
                cart.getTotalAmount(),
                null,
                "PENDING",
                "Book purchase fee");

        return  initPaymentResponse;
    }

    @Override
    public void updatePayment(NewPaymentNotificationRequest newPaymentNotificationRequest) {

        EventData eventData = newPaymentNotificationRequest.getEventData();

        String transactionReference = eventData.getTransactionReference();
        String incomingPaymentStatus = eventData.getPaymentStatus();
        double amount = eventData.getAmountPaid();

        Transactions feePayment = uploadPaymentService.findByTransactionReference(transactionReference);

        MonnifyResponse monnifyResponse = (MonnifyResponse) webClient.sendRequest(
                MONNIFY_BASE_URL,
                TRANSACTION_STATUS_URL+feePayment.getTransactionReference(),
                GET,
                null,
                getAuthorizationHeader(),
                APPLICATION_JSON,
                MonnifyResponse.class);

        Transaction confirmTransaction = new ObjectMapper().convertValue(monnifyResponse.getResponseBody(), Transaction.class);
        String confirmPaymentStatus = confirmTransaction.getPaymentStatus();

        boolean validRequest =
                        Objects.equals(eventData.getPaymentReference(), confirmTransaction.getPaymentReference()) &&
                        Objects.equals(incomingPaymentStatus, confirmPaymentStatus) &&
                        Objects.equals(confirmPaymentStatus, "PAID") &&
                        Double.compare(amount, Double.parseDouble(confirmTransaction.getAmountPaid())) == 0;

        if(validRequest) {
            feePayment.setPaymentMethod(confirmTransaction.getPaymentMethod());
            feePayment.setPaymentStatus(PaymentStatus.valueOf(confirmPaymentStatus.toUpperCase()));
        }
        else throw new UnauthorizedUserException("Invalid payload");
    }

    @Override
    public void confirmPayment(String paymentReference) {
        Transactions thisPayment = uploadPaymentService.findByReference(paymentReference);

        MonnifyResponse monnifyResponse = (MonnifyResponse) webClient.sendRequest(
                MONNIFY_BASE_URL,
                TRANSACTION_STATUS_URL+thisPayment.getTransactionReference(),
                GET,
                null,
                getAuthorizationHeader(),
                APPLICATION_JSON,
                MonnifyResponse.class);

        Transaction confirmTransaction = new ObjectMapper().convertValue(monnifyResponse.getResponseBody(), Transaction.class);
        String confirmPaymentStatus = confirmTransaction.getPaymentStatus();
        
        boolean validRequest =
                        Objects.equals(thisPayment.getPaymentReference(), confirmTransaction.getPaymentReference()) &&
                        Objects.equals(confirmPaymentStatus, "PAID") &&
                        Double.compare(thisPayment.getAmountPaid(), Double.parseDouble(confirmTransaction.getAmountPaid())) == 0;

        if(validRequest) {
            thisPayment.setPaymentStatus(PAID);
            thisPayment.setPaymentMethod(confirmTransaction.getPaymentMethod());

            Book thisBook = bookService.findByBookId(thisPayment.getBookId());
            thisBook.setBookStatus(BookStatus.ACTIVE);
        }
        else throw new UnauthorizedUserException("Invalid payload");
    }

    private NewMonnifyPaymentRequest buildRequest(NewPaymentRequest newPaymentRequest) {

        String redirectUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(PAYMENT_REDIRECT_URL)
                .toUriString();

        return
                NewMonnifyPaymentRequest.builder()
                .amount(newPaymentRequest.amount())
                .contractCode(MonnifyConfig.CONTRACT_CODE)
                .currencyCode(MonnifyConfig.NGN_CURRENCY_CODE)
                .customerEmail(newPaymentRequest.userEmail())
                .paymentDescription("New book upload _" +newPaymentRequest.bookId())
                .paymentMethods(MonnifyConfig.ALLOWED_PAYMENT_METHODS)
                .paymentReference(UUID.randomUUID().toString().replace("-", ""))
                .customerName(newPaymentRequest.customerName())
                .redirectUrl(redirectUrl)
                .build();
    }

    private MultiValueMap<String, String> getAuthorizationHeader() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", AUTHORIZATION_PREFIX + monnifyCredential.getAccessToken());
        return headers;
    }
}
