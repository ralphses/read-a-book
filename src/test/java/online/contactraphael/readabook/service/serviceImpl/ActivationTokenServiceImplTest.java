package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.UserRole;
import online.contactraphael.readabook.model.user.UserToken.ActivationToken;
import online.contactraphael.readabook.model.user.UserToken.TokenPurpose;
import online.contactraphael.readabook.repository.ActivationTokenRepository;
import online.contactraphael.readabook.service.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ActivationTokenServiceImplTest {

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @Mock
    private AppUserService appUserService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final String TOKEN_STRING = UUID.randomUUID().toString();
    private final String EMAIL = "eze.raph@gmail.com";
    private ActivationToken activationToken;
    private AppUser appUser;


    @InjectMocks
    private ActivationTokenServiceImpl underTest;

    @BeforeEach
    void setUp() {

        activationToken = ActivationToken.builder()
                .isUsed(Boolean.FALSE)
                .token(TOKEN_STRING)
                .createdAt(now())
                .email(EMAIL)
                .expiresAt(Instant.now().plus(1, ChronoUnit.MINUTES))
                .id(1L)
                .build();

        appUser = AppUser.builder()
                .userRole(UserRole.CUSTOMER)
                .isAccountNonLocked(true)
                .password(passwordEncoder.encode("password"))
                .isEnabled(true)
                .fullName("Raphael")
                .build();


        Mockito.when(activationTokenRepository.findByToken(TOKEN_STRING))
                .thenReturn(Optional.of(activationToken));

        Mockito.when(appUserService.findByEmail(EMAIL))
                .thenReturn(appUser);
    }

    @Test
    public void shouldCreateAndStoreNewActivationTokenWithValidUser() {

        AppUser appUser = AppUser.builder()
                .userRole(UserRole.CUSTOMER)
                .isAccountNonLocked(true)
                .password(passwordEncoder.encode("password"))
                .isEnabled(true)
                .fullName("Raphael")
                .build();

        underTest.newToken(TOKEN_STRING, appUser, TokenPurpose.SIGN_UP);
    }

    @Test
    public void shouldCreateAndStoreNewActivationTokenWithInNullUser() {
        assertThrows(NullPointerException.class, () -> underTest.newToken(TOKEN_STRING, null, TokenPurpose.SIGN_UP));
    }

    @Test
    public void shouldFetchActivationTokenWithValidToken() {
        ActivationToken activationToken = underTest.getActivationToken(TOKEN_STRING);
        assertEquals(activationToken.getToken(), TOKEN_STRING);
    }

    @Test
    public void shouldFetchActivationTokenWithInValidToken() {
        assertThrows(InvalidRequestParamException.class, () -> underTest.getActivationToken("TOKEN_STRING") );
    }

    @Test
    public void shouldFetchActivationTokenWithNullToken() {
        assertThrows(InvalidRequestParamException.class, () -> underTest.getActivationToken(null) );
    }

    @Test
    public void shouldActivateUserWithValidToken() {
        underTest.activate(TOKEN_STRING);
    }

    @Test
    public void shouldActivateUserWithInValidToken() {
        assertThrows(InvalidRequestParamException.class, () -> underTest.activate("TOKEN_STRING"));
    }

    @Test
    public void shouldActivateUserWithInUsedToken() {
        activationToken.setIsUsed(true);
        assertThrows(UnauthorizedUserException.class, () -> underTest.activate(TOKEN_STRING));
    }

    @Test
    public void shouldActivateUserWithExpiredToken() {
        activationToken.setExpiresAt(Instant.now());
        assertThrows(UnauthorizedUserException.class, () -> underTest.activate(TOKEN_STRING));
    }

    @Test
    public void shouldActivateUserWithExpiredTokenAndUsedToken() {
        activationToken.setExpiresAt(Instant.now());
        activationToken.setIsUsed(true);
        assertThrows(UnauthorizedUserException.class, () -> underTest.activate(TOKEN_STRING));
    }

    @Test
    public void shouldResendLinkWithValidEmailAndUserNotVerified() {
        appUser.setIsAccountNonLocked(false);
        underTest.resendLink("eze.raph@gmail.com");
    }

    @Test
    public void shouldResendLinkWithValidEmailAndUserVerified() {
        assertThrows(InvalidRequestParamException.class, () -> underTest.resendLink("eze.raph@gmail.com"));
    }

}