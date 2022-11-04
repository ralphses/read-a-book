package online.contactraphael.readabook.utility.event;

import lombok.Getter;
import lombok.Setter;
import online.contactraphael.readabook.model.user.AppUser;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private AppUser appUser;

    public RegistrationCompleteEvent(AppUser appUser) {
        super(appUser);
        this.appUser = appUser;
    }

}
