package online.contactraphael.readabook.configuration.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

@Data
@AllArgsConstructor
public class LogoutEventProperty {

    private Authentication authentication;
    private HttpServletRequest httpServletRequest;
}
