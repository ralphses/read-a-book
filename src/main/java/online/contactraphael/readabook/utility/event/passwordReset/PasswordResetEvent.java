package online.contactraphael.readabook.utility.event.passwordReset;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import online.contactraphael.readabook.model.user.AppUser;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class PasswordResetEvent extends ApplicationEvent {

    private String email;

    public PasswordResetEvent(String email) {
        super(email);
        this.email = email;
    }
}
