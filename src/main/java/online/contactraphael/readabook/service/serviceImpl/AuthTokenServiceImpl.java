package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.model.AuthToken;
import online.contactraphael.readabook.repository.AuthTokenRepository;
import online.contactraphael.readabook.service.service.AuthTokenService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AuthTokenRepository authTokenRepository;


    @Override
    public String login(Authentication authentication, HttpServletRequest httpServletRequest) {

        String user = authentication.getName();
        Optional<AuthToken> userOptional = authTokenRepository.findBySubject(user);

        if(userOptional.isPresent()) {

            AuthToken token = userOptional.get();

            if(token.getIsLoggedIn()) {
                log.info("Illegal login request from {}", httpServletRequest.getRemoteAddr());
                throw new UnauthorizedUserException("Illegal user from " + httpServletRequest.getRemoteAddr());
            }
            else {
                authTokenRepository.delete(token);
                log.info("New login request from {} for {}", httpServletRequest.getRemoteAddr(), user);
            }
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(1, ChronoUnit.HOURS);

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        String remoteAddr = httpServletRequest.getRemoteAddr();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuer("clicks")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user)
                .claim("claim", scope)
                .claim("origin", remoteAddr)
                .build();

        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
        saveNewToken(user, now, expiresAt, remoteAddr, tokenValue);

        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);

        return tokenValue;
    }

    @Override
    public void logout(Authentication authentication, HttpServletRequest httpServletRequest) {

        Optional<String> tokenOptional = Optional.ofNullable(((JwtAuthenticationToken) authentication)
                .getToken().getTokenValue());

        String user = authentication.getName();
        String requestRemoteAddress = httpServletRequest.getRemoteAddr();

        if(tokenOptional.isEmpty()) {
            log.debug("Empty token passed by user {} at {}", user, Instant.now());
            throw new UnauthorizedUserException("Invalid or empty token");
        }

        String requestTokenString = tokenOptional.get();

        try {

            Jwt decodedJwtString = jwtDecoder.decode(requestTokenString);
            AuthToken authToken = findTokenByRemoteAddress(requestRemoteAddress);

            if(!isValidToken(authToken, requestRemoteAddress, decodedJwtString)) {

                log.info("Invalid token passed {}", requestTokenString);
                throw new UnauthorizedUserException("Invalid or empty token");
            }

            authToken.setIsLoggedIn(false);
            authToken.setIsValid(false);

            log.info("User {} logged out from address {} ", user, requestRemoteAddress);

        }catch (JwtException e) {

            log.debug("Empty or invalid token passed by user {} at {} from {}",
                    user, Instant.now(), requestRemoteAddress);

            throw new UnauthorizedUserException("Invalid or empty token");
        }
    }

    @Override
    public boolean validateToken(String token) {
        AuthToken authToken = findByToken(token);
        return authToken.getIsLoggedIn() && authToken.getIsValid();
    }

    private AuthToken findByToken(String token) {
        return authTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedUserException("Invalid token " + token));
    }

    private boolean isValidToken(AuthToken authToken, String requestRemoteAddress, Jwt decodedString) {

        String tokenFromDb = authToken.getToken();

        return authToken.getIsLoggedIn() &&
                Objects.equals(requestRemoteAddress, decodedString.getClaimAsString("origin")) &&
                Objects.equals(authToken.getSubject(), decodedString.getSubject()) &&
                Objects.equals(tokenFromDb, decodedString.getTokenValue()) &&
                authToken.getExpiresAt().isAfter(Instant.now());
    }

    private void saveNewToken(String subject, Instant now, Instant expiresAt, String remoteAddr, String tokenValue) {
        AuthToken authToken = AuthToken.builder()
                .generatedAt(now)
                .expiresAt(expiresAt)
                .token(tokenValue)
                .subject(subject)
                .remoteAddress(remoteAddr)
                .isLoggedIn(true)
                .isValid(true)
                .build();

        authTokenRepository.save(authToken);
    }

    private AuthToken findTokenByRemoteAddress(String remoteAddress) {

        return authTokenRepository.findByRemoteAddress(remoteAddress)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or Illegal Token"));
    }
}
