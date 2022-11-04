package online.contactraphael.readabook.utility.monnify.webhook;

import lombok.Data;

@Data
public class NewPaymentNotificationRequest {
    private String eventType;
    private EventData eventData;
}
