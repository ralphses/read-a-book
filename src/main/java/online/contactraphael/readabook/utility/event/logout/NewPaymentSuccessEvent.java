package online.contactraphael.readabook.utility.event.logout;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class NewPaymentSuccessEvent extends ApplicationEvent {

    private String remoteAddress;

    public NewPaymentSuccessEvent(String remoteAddress) {
        super(remoteAddress);
        this.remoteAddress = remoteAddress;
    }
}
