package online.contactraphael.readabook.utility.monnify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InitPaymentResponse {
    private String transactionReference;
    private String paymentReference;
    private String merchantName;
    private String apiKey;
    private String[] enabledPaymentMethod;
    private String redirectUrl;
    private String checkoutUrl;
}
