package online.contactraphael.readabook.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.service.service.AuthTokenService;
import online.contactraphael.readabook.service.serviceImpl.CartService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;


@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class Cleaner {

    private final CartService cartService;
    private final AuthTokenService authTokenService;

    @Scheduled(fixedDelay = 80000000L)
    public void clearCart() {
        log.info("Cart clearance STARTED at {}", Instant.now());
        cartService.clearCartPerDay();
        log.info("Cart clearance COMPLETED at {}", Instant.now());

    }

    @Scheduled(fixedDelay = 80000000L)
    public void clearLogin() {
        authTokenService.clearLogin();
    }
}
