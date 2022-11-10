package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.model.dtos.PasswordModel;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.UserToken.PasswordResetToken;
import online.contactraphael.readabook.repository.PasswordResetTokenRepository;
import online.contactraphael.readabook.service.service.AppUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppUserService appUserService;

    public String initiatePasswordReset(String userEmail) {
        AppUser user = appUserService.findByEmail(userEmail);

        Instant now = Instant.now();
        String email = user.getEmail();

        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .createdAt(now)
                .passwordHash(UUID.nameUUIDFromBytes((user.getFullName()+ email).getBytes()).toString())
                .isUsed(false)
                .expiresAt(now.plus(5, ChronoUnit.MINUTES))
                .userEmail(email)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken.getPasswordHash();
    }

    public void resetPassword(String passwordHash, PasswordModel passwordModel) {

        if(!Objects.equals(passwordModel.confirmPassword(), passwordModel.password())) {
            throw new InvalidRequestParamException("New password must be equal to confirm password");
        }

        passwordResetTokenRepository.findByPasswordHash(passwordHash)
                .ifPresent(passwordResetToken -> {
                    if(passwordResetToken.getIsUsed() || passwordResetToken.getExpiresAt().isBefore(Instant.now())) {
                        throw new InvalidRequestParamException("Expired or used password link");
                    }
                    AppUser user = appUserService.findByEmail(passwordResetToken.getUserEmail());
                    user.setPassword(passwordEncoder.encode(passwordModel.password()));

                    passwordResetTokenRepository.delete(passwordResetToken);
                });
    }
}
