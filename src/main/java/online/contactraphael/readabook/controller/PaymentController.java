package online.contactraphael.readabook.controller;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.model.dtos.monnify.NewPaymentRequest;
import online.contactraphael.readabook.service.service.PaymentService;
import online.contactraphael.readabook.utility.ResponseMessage;
import online.contactraphael.readabook.utility.monnify.webhook.NewMonnifyPaymentNotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("api/v1/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(path = "/initialize")
    public ResponseEntity<ResponseMessage> initializePayment(@RequestBody @Valid NewPaymentRequest newPaymentRequest) {

        return ResponseEntity.ok(new ResponseMessage(
                "success",
                0,
                Map.of("paymentReference", paymentService.initNewBookPayment(newPaymentRequest))));
    }

    @PostMapping(path = "/notify")
    public ResponseEntity<ResponseMessage> notify(@RequestBody @Valid NewMonnifyPaymentNotificationRequest newMonnifyPaymentNotificationRequest) {
        paymentService.updatePayment(newMonnifyPaymentNotificationRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<ResponseMessage> confirm(@RequestParam("paymentReference") @Valid String paymentReference) {
        String confirmedPaymentReference = paymentService.confirmPayment(paymentReference);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("paymentReference", confirmedPaymentReference)));
    }
}
