package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.dtos.UserRegistrationRequest;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.ShortUser;
import online.contactraphael.readabook.repository.AppUserRepository;
import online.contactraphael.readabook.service.service.AppUserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public AppUser addUser(UserRegistrationRequest userRegistrationRequest) {

        String password = userRegistrationRequest.password();
        String confirmPassword = userRegistrationRequest.confirmPassword();

        if(!Objects.equals(password, confirmPassword)) {
            throw new InvalidRequestParamException("Password and Confirm password does not match");
        }

        String email = userRegistrationRequest.email();

        Optional<AppUser> userOptional = appUserRepository.findByEmail(email);

        if(userOptional.isPresent()) {
            throw new InvalidRequestParamException("User with email " + email + " already exist");
        }

        AppUser appUser = AppUser.builder()
                .fullName(userRegistrationRequest.fullName())
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

    @Override
    public List<ShortUser> getAll(Integer page) {

        try {
            return
                    appUserRepository
                            .findAll(PageRequest.of(page-1, 20))
                            .map(appUser -> ShortUser.builder()
                                    .fullName(appUser.getFullName())
                                    .id(appUser.getId())
                                    .email(appUser.getEmail())
                                    .userRole(appUser.getUserRole())
                                    .isAccountNonLocked(appUser.getIsAccountNonLocked())
                                    .build())
                            .stream().toList();

        }catch (Exception exception) {
            throw new InvalidRequestParamException("invalid page number");
        }
    }

    @Override
    public void deleteUser(String email) {
        appUserRepository.delete(findByEmail(email));
    }

    @Override
    public void suspendUser(String email) {
        findByEmail(email).setIsAccountNonLocked(false);
    }
}
