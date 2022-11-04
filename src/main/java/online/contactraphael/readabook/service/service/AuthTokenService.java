package online.contactraphael.readabook.service.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface AuthTokenService {

    String login(Authentication authentication, HttpServletRequest httpServletRequest);

    void logout(Authentication authentication, HttpServletRequest httpServletRequest);

    boolean validateToken(String token);
}
