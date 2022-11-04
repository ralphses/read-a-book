package online.contactraphael.readabook.controller;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.model.dtos.monnify.NewPaymentRequest;
import online.contactraphael.readabook.utility.ResponseMessage;
import online.contactraphael.readabook.service.serviceImpl.PaymentServiceImpl;
import online.contactraphael.readabook.utility.monnify.webhook.NewPaymentNotificationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("api/v1/pay")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @PostMapping(path = "/initialize")
    public ResponseEntity<ResponseMessage> initializePayment(@RequestBody @Valid NewPaymentRequest newPaymentRequest) {

        return ResponseEntity.ok(new ResponseMessage(
                "success",
                0,
                Map.of("paymentReference", paymentService.initPayment(newPaymentRequest))));
    }

    @PostMapping(path = "/notify")
    public ResponseEntity<ResponseMessage> notify(@RequestBody @Valid NewPaymentNotificationRequest newPaymentNotificationRequest) {
        paymentService.updatePayment(newPaymentNotificationRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<ResponseMessage> confirm(@RequestParam("paymentReference") @Valid String paymentReference) {
        paymentService.confirmPayment(paymentReference);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }
}
