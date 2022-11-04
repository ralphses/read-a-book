package online.contactraphael.readabook.utility.monnify.webhook;

import lombok.Data;

@Data
public class EventData {

    private String transactionReference;
    private String paymentReference;
    private double amountPaid;
    private String totalPayable;
    private String settlementAmount;
    private String paidOn;
    private String paymentStatus;
    private String paymentDescription;
    private String currency;
    private String paymentMethod;
    private Product product;
    private CardDetails cardDetails;
    private AccountDetails destinationAccountInformation;
    private AccountDetails[] paymentSourceInformation;
    private Customer customer;
    private Object metaData;
}
