package online.contactraphael.readabook.controller;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.service.serviceImpl.WishListService;
import online.contactraphael.readabook.utility.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/wishlist")
public class WishListController {

    private final WishListService wishListService;

    @PostMapping(path = "/add")
    public ResponseEntity<ResponseMessage> add(@RequestParam("bookCode") @NotBlank String bookCode,
                                               HttpServletRequest httpServletRequest) {
        wishListService.addBook(bookCode, httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @PutMapping(path = "/remove")
    public ResponseEntity<ResponseMessage> remove(@RequestParam("bookCode") @NotBlank String bookCode,
                                               HttpServletRequest httpServletRequest) {
        wishListService.removeBook(bookCode, httpServletRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of()));
    }

    @GetMapping(path = "/get")
    public ResponseEntity<ResponseMessage> get(HttpServletRequest httpServletRequest) {
        Set<Book> books = wishListService.getBooks(httpServletRequest.getRemoteAddr());
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("wishlist", books)));
    }
}
