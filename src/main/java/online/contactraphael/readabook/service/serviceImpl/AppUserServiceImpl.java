package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.dtos.UserRegistrationRequestRequest;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.repository.AppUserRepository;
import online.contactraphael.readabook.service.service.AppUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static online.contactraphael.readabook.model.user.UserRole.CUSTOMER;

@Service
@Transactional
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public AppUser addUser(UserRegistrationRequestRequest userRegistrationRequestRequest) {

        String password = userRegistrationRequestRequest.password();
        String confirmPassword = userRegistrationRequestRequest.confirmPassword();

        if(!Objects.equals(password, confirmPassword)) {
            throw new InvalidRequestParamException("Password and Confirm password does not match");
        }

        String email = userRegistrationRequestRequest.email();

        Optional<AppUser> userOptional = appUserRepository.findByEmail(email);
        if(userOptional.isPresent()) {
            throw new InvalidRequestParamException("User with email " + email + " already exist");
        }

        AppUser appUser = AppUser.builder()
                .fullName(userRegistrationRequestRequest.fullName())
                .email(email)
                .isAccountNonLocked(false)
                .userRole(CUSTOMER)
                .isEnabled(false)
                .password(passwordEncoder.encode(password))
                .build();

        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Unknown user " + email));
    }
}
