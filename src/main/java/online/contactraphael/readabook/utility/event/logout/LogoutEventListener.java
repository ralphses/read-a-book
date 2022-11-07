package online.contactraphael.readabook.utility.event.logout;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.configuration.security.LogoutEventProperty;
import online.contactraphael.readabook.service.service.AuthTokenService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutEventListener implements ApplicationListener<LogoutEvent> {

    private final AuthTokenService authTokenService;

    @Override
    public void onApplicationEvent(LogoutEvent logoutEvent) {
        LogoutEventProperty logoutEventProperty = logoutEvent.getLogoutEventProperty();
        authTokenService.logout(logoutEventProperty.getAuthentication(), logoutEventProperty.getHttpServletRequest());
    }
}
