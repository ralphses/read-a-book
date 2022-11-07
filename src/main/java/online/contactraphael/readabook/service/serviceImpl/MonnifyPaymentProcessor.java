package online.contactraphael.readabook.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.dtos.monnify.NewMonnifyPaymentRequest;
import online.contactraphael.readabook.model.dtos.monnify.NewPaymentRequest;
import online.contactraphael.readabook.model.payment.PaymentStatus;
import online.contactraphael.readabook.model.payment.Transactions;
import online.contactraphael.readabook.service.service.BookService;
import online.contactraphael.readabook.service.service.NotificationService;
import online.contactraphael.readabook.service.service.PaymentService;
import online.contactraphael.readabook.service.service.TransactionsService;
import online.contactraphael.readabook.utility.CustomWebClient;
import online.contactraphael.readabook.utility.event.paymentSuccess.NewPaymentSuccessEvent;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import online.contactraphael.readabook.utility.monnify.MonnifyConfig;
import online.contactraphael.readabook.utility.monnify.MonnifyCredential;
import online.contactraphael.readabook.utility.monnify.MonnifyResponse;
import online.contactraphael.readabook.utility.monnify.webhook.EventData;
import online.contactraphael.readabook.utility.monnify.webhook.NewMonnifyPaymentNotificationRequest;
import online.contactraphael.readabook.utility.monnify.webhook.Transaction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static online.contactraphael.readabook.configuration.main.AppConfig.PAYMENT_REDIRECT_URL;
import static online.contactraphael.readabook.model.book.BookStatus.ACTIVE;
import static online.contactraphael.readabook.model.payment.PaymentStatus.PAID;
import static online.contactraphael.readabook.utility.monnify.MonnifyConfig.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@Transactional
@RequiredArgsConstructor
public class MonnifyPaymentProcessor implements PaymentService {

    private final CustomWebClient webClient;
    private final HttpServletRequest httpServletRequest;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MonnifyCredential monnifyCredential;
    private final TransactionsService transactionsService;
    private final NotificationService notificationService;
    private final BookService bookService;


    @Override
    public InitPaymentResponse initNewBookPayment(NewPaymentRequest newPaymentRequest) {

        NewMonnifyPaymentRequest newMonnifyPaymentRequest = buildRequest(newPaymentRequest);
        InitPaymentResponse initPaymentResponse = initializeNewMonnifyPayment(newMonnifyPaymentRequest);

        //Save new uploadPaymentFee
        transactionsService.newPayment(
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
    public void updatePayment(NewMonnifyPaymentNotificationRequest newMonnifyPaymentNotificationRequest) {

        EventData eventData = newMonnifyPaymentNotificationRequest.getEventData();

        String transactionReference = eventData.getTransactionReference();
        String incomingPaymentStatus = eventData.getPaymentStatus();
        double amount = eventData.getAmountPaid();

        Transactions feePayment = transactionsService.findByTransactionReference(transactionReference);

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
    public InitPaymentResponse initializeNewMonnifyPayment(NewMonnifyPaymentRequest newMonnifyPaymentRequest) {

        MonnifyResponse response = (MonnifyResponse) webClient.sendRequest(
                MONNIFY_BASE_URL,
                INIT_TRANSACTION_URL,
                POST,
                newMonnifyPaymentRequest,
                getAuthorizationHeader(),
                APPLICATION_JSON,
                MonnifyResponse.class);

        return new ObjectMapper().convertValue(response.getResponseBody(), InitPaymentResponse.class);

    }

    @Override
    public String confirmPayment(String paymentReference) {

        Transactions thisPayment = transactionsService.findByReference(paymentReference);

        //Send request to monnify
        MonnifyResponse monnifyResponse = (MonnifyResponse) webClient.sendRequest(
                MONNIFY_BASE_URL,
                TRANSACTION_STATUS_URL+thisPayment.getTransactionReference(),
                GET,
                null,
                getAuthorizationHeader(),
                APPLICATION_JSON,
                MonnifyResponse.class);

        //GEt original transaction from monnify
        Transaction confirmTransaction = new ObjectMapper().convertValue(monnifyResponse.getResponseBody(), Transaction.class);

        boolean validRequest =
                        Objects.equals(thisPayment.getPaymentReference(), confirmTransaction.getPaymentReference()) &&
                        Objects.equals(confirmTransaction.getPaymentStatus(), "PAID") &&
                        Double.compare(thisPayment.getAmountPaid(), Double.parseDouble(confirmTransaction.getAmountPaid())) == 0;

        if(validRequest) {

            thisPayment.setPaymentStatus(PAID);
            thisPayment.setPaymentMethod(confirmTransaction.getPaymentMethod());

            Book thisBook = bookService.findByBookId(thisPayment.getBookId());

            if(thisPayment.getPurpose().equals("Book upload fee")) {

                thisBook.setBookStatus(ACTIVE);

            } else if (thisPayment.getPurpose().equals("Book purchase fee")) {

                //Todo: send book download URL to customer
                notificationService.sendEmailNotification(
                        List.of(thisPayment.getOwnerEmail()),
                        "info@awazone.net",
                        "Get your book here " + thisBook.getUrl(),
                        "Book purchase",
                        null);

                //Clear cart
                applicationEventPublisher.publishEvent(new NewPaymentSuccessEvent(httpServletRequest.getRemoteAddr()));
            }
            return paymentReference;
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
