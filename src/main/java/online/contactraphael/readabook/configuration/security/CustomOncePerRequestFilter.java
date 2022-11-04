package online.contactraphael.readabook.configuration.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.service.service.AuthTokenService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomOncePerRequestFilter extends OncePerRequestFilter {

    private final AuthTokenService authTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(httpServletRequest.getServletPath().startsWith("/auth")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        try {
            String token = Optional.ofNullable(httpServletRequest.getHeader("Authorization"))
                    .orElseThrow(() -> new UnauthorizedUserException("Illegal User"));

            if(!authTokenService.validateToken(token)) {
                throw new UnauthorizedUserException("Illegal User");
            }

            filterChain.doFilter(httpServletRequest, httpServletResponse);

        }catch (Exception exception) {
            log.info("Unauthorized request from {}", httpServletRequest.getRemoteAddr());
            throw new UnauthorizedUserException("Illegal User");
        }
    }
}
