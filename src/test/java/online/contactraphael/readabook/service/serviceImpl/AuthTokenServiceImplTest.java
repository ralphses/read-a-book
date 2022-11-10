package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.model.AuthToken;
import online.contactraphael.readabook.model.user.UserToken.ActivationToken;
import online.contactraphael.readabook.repository.AuthTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class AuthTokenServiceImplTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private CartService cartService;

    @Mock
    private WishListService wishListService;

    @InjectMocks
    AuthTokenServiceImpl underTest;

    @Mock
    Authentication authentication;

    @Mock
    HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {

    }

    @Test
    public void test() {
    }
}