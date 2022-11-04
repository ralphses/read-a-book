package online.contactraphael.readabook.utility.monnify.webhook;

import lombok.Data;

@Data
public class CardDetails {
    private String last4;
    private String expMonth;
    private String expYear;
    private String bin;
    private String cardType;
    private String bankCode;
    private String bankName;
    private String countryCode;
    private String cardToken;
    private String maskedPan;
    private boolean reusable;
    private boolean supportsTokenization;
}
