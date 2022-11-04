package online.contactraphael.readabook.controller;

import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.dtos.GeneralBookUploadRequest;
import online.contactraphael.readabook.model.dtos.NewBookUploadRequest;
import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.model.response.FileUploadResponse;
import online.contactraphael.readabook.service.service.BookService;
import online.contactraphael.readabook.utility.ResponseMessage;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/v1/book")
public class BookController {

    private final BookService bookService;

    @PostMapping(path = "/add")
    public ResponseEntity<ResponseMessage> addBook(@RequestBody @Valid GeneralBookUploadRequest generalBookUploadRequest) {
        Map<String, Object> response = bookService.addNewBook(generalBookUploadRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, response));

    }

    @PostMapping(path = "/add/{userEmail}")
    @PreAuthorize("#userEmail == authentication.principal")
    @Secured({"ADMIN", "CUSTOMER"})
    public ResponseEntity<ResponseMessage> addNewBook(@PathVariable("userEmail") @NotBlank String userEmail,
                                                      @RequestBody @Valid NewBookUploadRequest newBookUploadRequest) {
        String newBookId = bookService.addNewBook(userEmail, newBookUploadRequest);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("bookId", newBookId)));
    }

    @PostMapping(path = "/add/one")
    public ResponseEntity<ResponseMessage> upload(@RequestParam("paymentReference") @NotBlank String paymentReference,
                                                  @RequestParam(value = "bookCode", required = false) String bookCode,
                                                  @RequestPart("file") MultipartFile book) {
        String downloadUrl = bookService.upload(paymentReference, book, bookCode);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("url", downloadUrl)));
    }

    @GetMapping(path = "/download")
    public ResponseEntity<Resource> downloadBook(@RequestParam("bookCode") String bookCode, HttpServletRequest httpServletRequest) {
        FileUploadResponse book = bookService.fetchBook(httpServletRequest, bookCode);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(book.getFileType()))
                .body(book.getResource());
    }

    @GetMapping(path = "/get/{page}")
    public ResponseEntity<ResponseMessage> getAllBooks(@PathVariable Integer page) {
        List<Book> books = bookService.getAll(page);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("books", books)));
    }

    @GetMapping(path = "/get/one")
    public ResponseEntity<ResponseMessage> getAllBooks(@RequestParam("bookCode") @NotBlank String bookCode) {
        Book book = bookService.findByBookId(bookCode);
        return ResponseEntity.ok(new ResponseMessage("success", 0, Map.of("books", book)));
    }

}
