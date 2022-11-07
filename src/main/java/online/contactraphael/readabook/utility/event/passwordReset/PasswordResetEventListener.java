package online.contactraphael.readabook.utility.event.passwordReset;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.service.service.NotificationService;
import online.contactraphael.readabook.service.serviceImpl.PasswordResetTokenService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PasswordResetEventListener implements ApplicationListener<PasswordResetEvent> {

    private final NotificationService notificationService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Override
    public void onApplicationEvent(PasswordResetEvent passwordResetEvent) {
        String email = passwordResetEvent.getEmail();
        String passwordHash = passwordResetTokenService.initiatePasswordReset(email);

        String resetLink = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/auth/reset-password")
                .queryParam("hash", passwordHash)
                .toUriString();

        String message = "Click here to reset your password -> " + resetLink;
        notificationService.sendEmailNotification(List.of(email), "no-reply@contactraphael.com", message, "password reset", null);
    }
}
