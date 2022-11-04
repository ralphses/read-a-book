package online.contactraphael.readabook.controller;

import online.contactraphael.readabook.model.dtos.UserRegistrationRequestRequest;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.service.service.ActivationTokenService;
import online.contactraphael.readabook.service.service.AppUserService;
import online.contactraphael.readabook.service.serviceImpl.AuthTokenServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.utility.ResponseMessage;
import online.contactraphael.readabook.utility.event.RegistrationCompleteEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
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

    private final AuthTokenServiceImpl authTokenServiceImpl;
    private final ActivationTokenService activationTokenService;
    private final AppUserService appUserService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> addUser(
            @RequestBody @Valid UserRegistrationRequestRequest userRegistrationRequestRequest) {

        AppUser appUser = appUserService.addUser(userRegistrationRequestRequest);

        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(appUser));
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @PostMapping(path = "/login")
    public String login(Authentication authentication, HttpServletRequest httpServletRequest) {

        log.debug("Token requested for user {} ", authentication.getName());
        String token = authTokenServiceImpl.login(authentication, httpServletRequest);
        log.debug("Token granted for user {} ", authentication.getName());

        return token;
    }

    @PostMapping(path = "/logout")
    public ResponseEntity<ResponseMessage> logout(Authentication authentication, HttpServletRequest httpServletRequest) {

        log.info("logout request by {} ", authentication.getName());
        authTokenServiceImpl.logout(authentication, httpServletRequest);
        log.info("logout successful for user {} ", authentication.getName());

        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
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

}
