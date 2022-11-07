package online.contactraphael.readabook.utility.monnify.webhook;

import lombok.Data;

@Data
public class NewMonnifyPaymentNotificationRequest {
    private String eventType;
    private EventData eventData;
}
