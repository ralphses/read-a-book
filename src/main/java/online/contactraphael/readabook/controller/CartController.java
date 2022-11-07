package online.contactraphael.readabook.controller;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.model.dtos.ShortCartBook;
import online.contactraphael.readabook.service.serviceImpl.CartService;
import online.contactraphael.readabook.utility.ResponseMessage;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cart")
public class CartController {

    private final CartService cartService;

    @PostMapping(path = "/add")
    public ResponseEntity<ResponseMessage> add(@RequestParam("bookCode") @NotBlank String bookCode,
                                               @RequestParam("quantity") @NotBlank String quantity,
                                               HttpServletRequest httpServletRequest) {
        cartService.addBook(bookCode, quantity, httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<ResponseMessage> getCartBooks(HttpServletRequest httpServletRequest) {
        Set<ShortCartBook> cartBooks = cartService.getCartBooks(httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("bookItems", cartBooks)));
    }

    @PutMapping(path = "/remove")
    public ResponseEntity<ResponseMessage> removeBookFromCart(@RequestParam("bookCode") @NotBlank String bankCode,
                                                              HttpServletRequest httpServletRequest) {
        cartService.removeBookFromCart(bankCode, httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @PutMapping(path = "/update-qty")
    public ResponseEntity<ResponseMessage> setNewBookQty(@RequestParam("bookCode") @NotBlank String bankCode,
                                                              @RequestParam("newQty") Integer qty,
                                                              HttpServletRequest httpServletRequest) {
        cartService.setNewBookQty(bankCode, qty, httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @PostMapping(path = "/checkout")
    public ResponseEntity<ResponseMessage> checkout(@RequestParam("userEmail") @NotBlank String userEmail,
                                                         HttpServletRequest httpServletRequest) {
        InitPaymentResponse checkoutDetails = cartService.checkout(userEmail, httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("checkout", checkoutDetails)));
    }

}
