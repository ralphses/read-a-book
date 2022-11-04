package online.contactraphael.readabook.model.dtos.monnify;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewMonnifyPaymentRequest {
    private Double amount;
    private String customerName;
    private String customerEmail;
    private String paymentReference;
    private String paymentDescription;
    private String currencyCode;
    private String contractCode;
    private String redirectUrl;
    private String[] paymentMethods;
}
