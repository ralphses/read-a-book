package online.contactraphael.readabook.utility.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.service.service.ActivationTokenService;
import online.contactraphael.readabook.service.service.NotificationService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

import static online.contactraphael.readabook.model.user.UserToken.TokenPurpose.SIGN_UP;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final ActivationTokenService activationTokenService;
    private final NotificationService notificationService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent registrationCompleteEvent) {

        AppUser appUser = registrationCompleteEvent.getAppUser();
        String token = UUID.randomUUID().toString();
        String sender = "no-reply@contactraphael.com";
        String subject = "Activate account";

        String urlLink = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("auth/activate")
                .queryParam("token", token)
                .toUriString();


        notificationService.sendEmailNotification(List.of(appUser.getEmail()), sender, urlLink, subject);

        activationTokenService.newToken(token, appUser, SIGN_UP);

    }
}
