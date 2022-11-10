package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.UserRole;
import online.contactraphael.readabook.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CustomUserDetailsService underTest;

    @Test
    public void itShouldLoadUserDetails() {
        String email = "eze.raph@gmail.com";
        AppUser appUser = AppUser.builder()
                .email(email)
                .fullName("raphael")
                .isEnabled(true)
                .password(passwordEncoder.encode("password"))
                .id(1L)
                .userRole(UserRole.CUSTOMER)
                .isAccountNonLocked(true)
                .build();
        Mockito.when(appUserRepository.findByEmail(email))
                .thenReturn(Optional.of(appUser));

        underTest.loadUserByUsername(email);
    }

    @Test
    public void itShouldNotLoadUserDetailsWithInvalidEmail() {
        String email = "eze.raph@gmail.com";
        AppUser appUser = AppUser.builder()
                .email(email)
                .fullName("raphael")
                .isEnabled(true)
                .password(passwordEncoder.encode("password"))
                .id(1L)
                .userRole(UserRole.CUSTOMER)
                .isAccountNonLocked(true)
                .build();
        Mockito.when(appUserRepository.findByEmail(email))
                .thenReturn(Optional.of(appUser));

        assertThrows(UsernameNotFoundException.class, () -> underTest.loadUserByUsername("email"));
    }
}