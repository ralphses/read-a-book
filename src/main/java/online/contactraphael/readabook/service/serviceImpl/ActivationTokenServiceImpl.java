package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.model.user.UserToken.ActivationToken;
import online.contactraphael.readabook.model.user.UserToken.TokenPurpose;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.repository.ActivationTokenRepository;
import online.contactraphael.readabook.service.service.ActivationTokenService;
import online.contactraphael.readabook.service.service.AppUserService;
import online.contactraphael.readabook.utility.event.RegistrationCompleteEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ActivationTokenServiceImpl implements ActivationTokenService {

    private final ActivationTokenRepository activationTokenRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AppUserService appUserService;

    @Override
    public void newToken(String tokenString, AppUser appUser, TokenPurpose tokenPurpose) {

        Instant now = Instant.now();

        ActivationToken activationToken = ActivationToken.builder()
                .createdAt(now)
                .token(tokenString)
                .expiresAt(now.plus(10, ChronoUnit.MINUTES))
                .isUsed(false)
                .email(appUser.getEmail())
                .purpose(tokenPurpose)
                .build();

        activationTokenRepository.save(activationToken);
    }

    @Override
    public void activate(String token) {

        ActivationToken activationToken = getActivationToken(token);

        boolean validToken =
                !activationToken.getIsUsed() &&
                activationToken.getExpiresAt().isAfter(Instant.now());

        if(validToken) {
            AppUser appUser = appUserService.findByEmail(activationToken.getEmail());
            appUser.setIsAccountNonLocked(true);
            appUser.setIsEnabled(true);

            activationToken.setIsUsed(true);
        }
        else throw new UnauthorizedUserException("User already verified " + token);
    }

    @Override
    public void resendLink(String email) {

        Optional<ActivationToken> oldToken = activationTokenRepository.findByEmail(email);
        oldToken.ifPresent(activationTokenRepository::delete);

        AppUser appUser = appUserService.findByEmail(email);
        if (appUser.getIsAccountNonLocked()) {
            throw new InvalidRequestParamException("User already verified for " +email);
        }
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(appUser));

    }

    private ActivationToken getActivationToken(String token) {
        return activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRequestParamException("Invalid activation token " + token));
    }
}
