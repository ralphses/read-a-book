package online.contactraphael.readabook.configuration.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.utility.event.logout.LogoutEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("logout request by {} ", authentication.getName());
        applicationEventPublisher.publishEvent(new LogoutEvent(new LogoutEventProperty(authentication, request)));
        log.info("logout successful for user {} ", authentication.getName());

    }
}
