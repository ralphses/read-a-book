package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.dtos.PasswordModel;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.UserRole;
import online.contactraphael.readabook.model.user.UserToken.PasswordResetToken;
import online.contactraphael.readabook.repository.PasswordResetTokenRepository;
import online.contactraphael.readabook.service.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class PasswordResetTokenServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private PasswordResetTokenService underTest;

    private final String EMAIL = "eze.raph@gmail.com";
    private final String passwordHash = UUID.randomUUID().toString();
    private AppUser appUser;
    private PasswordResetToken passwordResetToken;


    @BeforeEach
    void setUp() {

        appUser = AppUser.builder()
                .email(EMAIL)
                .fullName("raphael")
                .isEnabled(true)
                .password(passwordEncoder.encode("password"))
                .id(1L)
                .userRole(UserRole.CUSTOMER)
                .isAccountNonLocked(true)
                .build();

        Mockito.when(appUserService.findByEmail(EMAIL))
                .thenReturn(appUser);

        passwordResetToken = PasswordResetToken.builder()
                .userEmail(EMAIL)
                .expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES))
                .isUsed(false)
                .passwordHash(passwordHash)
                .createdAt(Instant.now())
                .id(1L)
                .build();

        Mockito.when(passwordResetTokenRepository.save(passwordResetToken))
                .thenReturn(passwordResetToken);
    }

    @Test
    public void itShouldInitiatePasswordResetWIthValidEmail() {
        underTest.initiatePasswordReset(EMAIL);
    }

    @Test
    public void itShouldInitiatePasswordResetWIthInValidEmail() {
        Mockito.when(appUserService.findByEmail(EMAIL))
                .thenReturn(null);
        assertThrows(NullPointerException.class, () -> underTest.initiatePasswordReset(EMAIL));
    }

    @Test
    public void itShouldResetPassword() {
        String password = "password";
        String confirmPassword = "password";
        PasswordModel passwordModel = new PasswordModel(password, confirmPassword);

        Mockito.when(passwordResetTokenRepository.findByPasswordHash(passwordHash))
                .thenReturn(Optional.of(passwordResetToken));

        Mockito.when(appUserService.findByEmail(EMAIL))
                .thenReturn(appUser);

        underTest.resetPassword(passwordHash, passwordModel);

    }

    @Test
    public void itShouldResetPasswordWithPasswordNotMatch() {
        String password = "password1";
        String confirmPassword = "password";
        PasswordModel passwordModel = new PasswordModel(password, confirmPassword);

        Mockito.when(passwordResetTokenRepository.findByPasswordHash(passwordHash))
                .thenReturn(Optional.of(passwordResetToken));

        Mockito.when(appUserService.findByEmail(EMAIL))
                .thenReturn(appUser);

        assertThrows(InvalidRequestParamException.class, () -> underTest.resetPassword(passwordHash, passwordModel));
    }


    @Test
    public void itShouldResetPasswordWithUsedToken() {
        String password = "password";
        String confirmPassword = "password";
        PasswordModel passwordModel = new PasswordModel(password, confirmPassword);

        Mockito.when(passwordResetTokenRepository.findByPasswordHash(passwordHash))
                .thenReturn(Optional.of(passwordResetToken));

        Mockito.when(appUserService.findByEmail(EMAIL))
                .thenReturn(appUser);

        passwordResetToken.setIsUsed(true);

        assertThrows(InvalidRequestParamException.class, () -> underTest.resetPassword(passwordHash, passwordModel));
    }
}