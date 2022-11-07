package online.contactraphael.readabook.utility.event.logout;

import lombok.Getter;
import lombok.Setter;
import online.contactraphael.readabook.configuration.security.LogoutEventProperty;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class LogoutEvent extends ApplicationEvent {

    private LogoutEventProperty logoutEventProperty;

    public LogoutEvent(LogoutEventProperty logoutEventProperty) {
        super(logoutEventProperty);
        this.logoutEventProperty = logoutEventProperty;
    }
}
