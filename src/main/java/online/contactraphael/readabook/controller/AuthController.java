package online.contactraphael.readabook.controller;

import online.contactraphael.readabook.model.dtos.PasswordModel;
import online.contactraphael.readabook.model.dtos.UserRegistrationRequest;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.service.service.ActivationTokenService;
import online.contactraphael.readabook.service.service.AppUserService;
import online.contactraphael.readabook.service.service.AuthTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.utility.ResponseMessage;
import online.contactraphael.readabook.utility.event.passwordReset.PasswordResetEvent;
import online.contactraphael.readabook.utility.event.registration.RegistrationCompleteEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {

    private final AuthTokenService authTokenService;
    private final ActivationTokenService activationTokenService;
    private final AppUserService appUserService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> addUser(
            @RequestBody @Valid UserRegistrationRequest userRegistrationRequest) {

        AppUser appUser = appUserService.addUser(userRegistrationRequest);

        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(appUser));
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ResponseMessage> login(Authentication authentication, HttpServletRequest httpServletRequest) {

        log.debug("Token requested for user {} ", authentication.getName());
        String token = authTokenService.login(authentication, httpServletRequest);
        log.debug("Token granted for user {} ", authentication.getName());

        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("accessToken", token)));
    }

    @PostMapping(path = "/activate")
    public ResponseEntity<ResponseMessage> activateUser(@RequestParam("token") @NotBlank String token) {

        log.info("Activation requested for token {}", token);
        activationTokenService.activate(token);

        log.info("Activation successful for token {}", token);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @PostMapping(path = "/resendLink")
    public ResponseEntity<ResponseMessage> resendLink(@RequestParam("email") @NotBlank String email) {

        log.info("Activation reset link requested for {}", email);
        activationTokenService.resendLink(email);

        log.info("Activation reset link sent for {}", email);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @PostMapping(path = "/password-reset")
    @PreAuthorize("#email == authentication.principal")
    public ResponseEntity<ResponseMessage> initPasswordReset(@RequestParam("email") @NotBlank String email) {

        applicationEventPublisher.publishEvent(new PasswordResetEvent(email));
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("message", "password reset link sent to " + email)));
    }

    @PutMapping(path = "/password-reset")
    public ResponseEntity<ResponseMessage> passWordReset(@RequestBody @Valid PasswordModel passwordModel,
                                                         @RequestParam("hash") String passwordHash) {
        authTokenService.resetPassword(passwordModel, passwordHash);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }
}