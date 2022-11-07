package online.contactraphael.readabook.utility.event.logout;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.repository.CartRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class NewPaymentSuccessEventListener implements ApplicationListener<NewPaymentSuccessEvent> {

    private final CartRepository cartRepository;

    @Override
    public void onApplicationEvent(NewPaymentSuccessEvent newPaymentSuccessEvent) {

        cartRepository.findByUserAddress(newPaymentSuccessEvent.getRemoteAddress())
                .ifPresent(cart -> {
                    if (cart.getCreatedAt().plus(2, ChronoUnit.DAYS).isBefore(Instant.now())) {
                        cartRepository.delete(cart);
                    }
                });

    }
}
