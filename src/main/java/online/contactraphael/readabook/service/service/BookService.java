package online.contactraphael.readabook.service.service;

import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.dtos.GeneralBookUploadRequest;
import online.contactraphael.readabook.model.dtos.NewBookUploadRequest;
import online.contactraphael.readabook.model.dtos.ShortBook;
import online.contactraphael.readabook.model.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface BookService {
    String  addNewBook(String userEmail, NewBookUploadRequest newBookUploadRequest);

    String upload(String paymentReference, MultipartFile book, String bookCode);
    Book findByBookId(String bookId);

    FileUploadResponse fetchBook(HttpServletRequest httpServletRequest, String bookCode);

    List<Book> getAll(Integer page);

    Map<String, Object> addNewBook(GeneralBookUploadRequest generalBookUploadRequest);

    void updateBook(String bookCode, GeneralBookUploadRequest generalBookUploadRequest);

    List<ShortBook> findAll(Integer page);

    void deactivateBook(String bookCode);
}
