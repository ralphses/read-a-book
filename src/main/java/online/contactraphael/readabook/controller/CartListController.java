package online.contactraphael.readabook.controller;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.service.serviceImpl.CartListService;
import online.contactraphael.readabook.utility.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/wishlist")
public class CartListController {

    private final CartListService cartListService;

    @PostMapping(path = "/add")
    public ResponseEntity<ResponseMessage> add(@RequestParam("bookCode") @NotBlank String bookCode,
                                               @RequestParam("quantity") @NotBlank String quantity,
                                               HttpServletRequest httpServletRequest) {
        cartListService.addBook(bookCode, quantity, httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }
}
