package online.contactraphael.readabook.utility.monnify.webhook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.contactraphael.readabook.utility.monnify.webhook.AccountDetails;
import online.contactraphael.readabook.utility.monnify.webhook.CardDetails;
import online.contactraphael.readabook.utility.monnify.webhook.Customer;
import online.contactraphael.readabook.utility.monnify.webhook.Product;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    private String transactionReference;
    private String paymentReference;
    private String amountPaid;
    private String totalPayable;
    private String settlementAmount;
    private String paidOn;
    private String paymentStatus;
    private String paymentDescription;
    private String currency;
    private String paymentMethod;
    private Product product;
    private CardDetails cardDetails;
    private AccountDetails accountDetails;
    private AccountDetails[] accountPayments;
    private Customer customer;
    private Object metaData;
}
