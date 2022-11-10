package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.dtos.UserRegistrationRequest;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.ShortUser;
import online.contactraphael.readabook.model.user.UserRole;
import online.contactraphael.readabook.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AppUserServiceImplTest {

    private final String EMAIL = "eze.raph@gmail.com";

    @InjectMocks
    private AppUserServiceImpl underTest;

    @Mock
    private Pageable pageable;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = AppUser.builder()
                .userRole(UserRole.CUSTOMER)
                .email(EMAIL)
                .isAccountNonLocked(true)
                .password(passwordEncoder.encode("password"))
                .isEnabled(true)
                .fullName("Raphael")
                .build();

        Mockito.when(appUserRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(appUser));
    }

    @Test
    public void shouldAddNewUserWithValidParams() {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Raphael",
                "eze1.raph@gmail.com",
                "password",
                "password"
        );

        underTest.addUser(userRegistrationRequest);
    }

    @Test
    public void shouldAddNewUserWithPasswordNotMatch() {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Raphael",
                "eze1.raph@gmail.com",
                "password",
                "password2"
        );
        assertThrows(InvalidRequestParamException.class, () -> underTest.addUser(userRegistrationRequest) );
    }

    @Test
    public void shouldAddNewUserWithExistingUserEmail() {
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest(
                "Raphael",
                "eze.raph@gmail.com",
                "password",
                "password"
        );
        assertThrows(InvalidRequestParamException.class, () -> underTest.addUser(userRegistrationRequest) );
    }

    @Test
    public void shouldFindUserByEmailWhenValidEmail() {
        AppUser expectedUser = underTest.findByEmail(EMAIL);
        assertEquals(expectedUser.getEmail(), EMAIL);
    }

    @Test
    public void shouldFindUserByEmailWhenInValidEmail() {
        assertThrows(ResourceNotFoundException.class, () -> underTest.findByEmail("EMAIL"));
    }

    @Test
    public void shouldFetchAllUserWithValidPageNumber(){
        List<AppUser> userList = List.of(appUser);
        Pageable pageable1 = PageRequest.of(0, 20);

        Page<AppUser> page = new PageImpl<>(userList);
        Mockito.when(appUserRepository.findAll(pageable1))
                .thenReturn(page);

        List<ShortUser> all = underTest.getAll(1);

        assertEquals(all.get(0).getFullName(), "Raphael");

    }

    @Test
    public void shouldFetchAllUserWithValidPageNumberWithValidList(){
        List<AppUser> userList = List.of(appUser);
        Pageable pageable1 = PageRequest.of(0, 20);

        Page<AppUser> page = new PageImpl<>(userList);
        Mockito.when(appUserRepository.findAll(pageable1))
                .thenReturn(page);


        assertNotNull(underTest.getAll(1));

    }

    @Test
    public void shouldFetchAllUserWithInvalidPageNumber(){
        List<AppUser> userList = List.of(appUser);
        Pageable pageable1 = PageRequest.of(0, 20);

        Page<AppUser> page = new PageImpl<>(userList);
        Mockito.when(appUserRepository.findAll(pageable1))
                .thenReturn(page);
        assertThrows(InvalidRequestParamException.class, () -> underTest.getAll(2));

    }

    @Test
    public void itShouldDeleteAUserWithValidEmail() {
        underTest.deleteUser(EMAIL);
    }

    @Test
    public void itShouldDeleteAUserWithInValidEmail() {
        assertThrows(ResourceNotFoundException.class, () -> underTest.deleteUser("EMAIL"));
    }

    @Test
    public void itShouldSuspendUserWithValidEmail() {
        underTest.suspendUser(EMAIL);
    }

    @Test
    public void itShouldSuspendUserWithInValidEmail() {
        assertThrows(ResourceNotFoundException.class, () -> underTest.suspendUser("EMAIL"));
    }
}