package online.contactraphael.readabook.utility.monnify.webhook;

import lombok.Data;

@Data
public class AccountDetails {
    private String accountName;
    private String accountNumber;
    private String bankCode;
    private String amountPaid;
}
