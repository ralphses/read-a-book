package online.contactraphael.readabook.service.serviceImpl;

import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.book.BookStatus;
import online.contactraphael.readabook.model.book.BookType;
import online.contactraphael.readabook.model.book.BookUploader;
import online.contactraphael.readabook.model.dtos.GeneralBookUploadRequest;
import online.contactraphael.readabook.model.dtos.NewBookUploadRequest;
import online.contactraphael.readabook.model.dtos.ShortBook;
import online.contactraphael.readabook.model.payment.Transactions;
import online.contactraphael.readabook.model.response.FileUploadResponse;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.repository.AppUserRepository;
import online.contactraphael.readabook.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.repository.BookUploaderRepository;
import online.contactraphael.readabook.service.service.BookService;
import online.contactraphael.readabook.service.service.TransactionsService;
import online.contactraphael.readabook.utility.uploads.FileManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static online.contactraphael.readabook.model.book.BookStatus.ACTIVE;
import static online.contactraphael.readabook.model.book.BookStatus.INACTIVE;
import static online.contactraphael.readabook.model.book.BookType.FEATURED;
import static online.contactraphael.readabook.model.book.BookType.WAVED;
import static online.contactraphael.readabook.model.payment.PaymentStatus.PAID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AppUserRepository appUserRepository;
    private final TransactionsService transactionsService;
    private final BookUploaderRepository bookUploaderRepository;
    private final FileManager fileManager;


    @Override
    public String addNewBook(String userEmail, NewBookUploadRequest newBookUploadRequest) {

        String bookId = newBookId();

        Book newBook = buildBookObject(
                newBookUploadRequest.yearPublished(),
                INACTIVE,
                newBookUploadRequest.author(),
                FEATURED,
                newBookUploadRequest.edition(),
                bookId,
                newBookUploadRequest.licence(),
                newBookUploadRequest.noOfPages(),
                newBookUploadRequest.summary(),
                newBookUploadRequest.title(),
                "",
                newBookUploadRequest.price());

        Optional<AppUser> userOptional = appUserRepository.findByEmail(userEmail);

        userOptional.ifPresent(appUser -> {

            BookUploader bookUploader = BookUploader.builder()
                    .name(userOptional.get().getFullName())
                    .email(userEmail)
                    .build();

            bookUploaderRepository.save(bookUploader);
            newBook.setUploadedBy(bookUploader);
        });

        bookRepository.save(newBook);
        return bookId;
    }

    private String newBookId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public String upload(String paymentReference, MultipartFile book, String bookCode) {

        if(paymentReference.startsWith("COUPON_") && bookCode == null) {
            throw new UnauthorizedUserException("Illegal upload with paymentReference " + paymentReference);
        }

        Transactions transactions = transactionsService.findByReference(paymentReference);

        boolean isWavedBook =
                Objects.equals(transactions.getTransactionReference(), paymentReference) &&
                        Objects.equals(bookCode, transactions.getBookId()) &&
                        Objects.equals(transactions.getPaymentMethod(), "NONE") &&
                        transactions.getAmountPaid() == 0.0;

        if(!transactions.getPaymentStatus().equals(PAID)) {
            throw new UnauthorizedUserException("Invalid payment reference " + paymentReference);
        }

        String bookId = (isWavedBook) ? bookCode : transactions.getBookId();
        Book thisBook = findById(bookId);

        if(!thisBook.getBookStatus().equals(ACTIVE)) {
            throw new UnauthorizedUserException("Book not yet active");
        }

        String fileName = bookId + ".pdf";
        String fileDownloadUri = getFileDownloadUrl(fileName);

        //Save file
        fileManager.saveFile(book, fileName);

        thisBook.setUrl(fileDownloadUri);

        return fileDownloadUri;
    }

    private String getFileDownloadUrl(String fileName) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("api/v1/book/download")
                .queryParam("bookCode", fileName)
                .toUriString();
    }

    @Override
    public Book findByBookId(String bookId) {
        return bookRepository.findByBookCode(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid book code " + bookId));
    }

    @Override
    public FileUploadResponse fetchBook(HttpServletRequest httpServletRequest, String bookCode) {
        return fileManager.getFileUploadResponse(httpServletRequest, bookCode);
    }

    @Override
    public Map<String, Object> addNewBook(GeneralBookUploadRequest generalBookUploadRequest) {

        String bookId = newBookId();
        String fileName = bookId + ".pdf";

        String downloadUrl = "getFileDownloadUrl(fileName)";
        String uploaderEmail = generalBookUploadRequest.email();
        String bookTitle = generalBookUploadRequest.title();

        Book newBook = buildBookObject(
                generalBookUploadRequest.yearPublished(),
                ACTIVE,
                generalBookUploadRequest.author(),
                WAVED,
                generalBookUploadRequest.edition(),
                bookId,
                generalBookUploadRequest.licence(),
                generalBookUploadRequest.noOfPages(),
                generalBookUploadRequest.summary(),
                bookTitle,
                downloadUrl,
                generalBookUploadRequest.amount()
        );

        String uploaderName = generalBookUploadRequest.name();
        BookUploader bookUploader = bookUploaderRepository.findByEmail(uploaderEmail)
                .orElse(BookUploader.builder()
                        .email(uploaderEmail)
                        .name(uploaderName)
                        .build());

        newBook.setUploadedBy(bookUploader);

        String paymentCoupon = "COUPON_"+(UUID.nameUUIDFromBytes((uploaderEmail+ uploaderName+ bookTitle).getBytes())
                .toString().replace("-", "").substring(0, 8));

        //Create new waved payment
        transactionsService.newPayment(
                bookId,
                uploaderEmail,
                paymentCoupon,
                paymentCoupon,
                0.0,
                "NONE",
                "PAID",
                "Book upload fee"
        );

        bookUploaderRepository.save(bookUploader);
        bookRepository.save(newBook);

        return Map.of("bookCode", bookId, "paymentCoupon", paymentCoupon);
    }

    @Override
    public void updateBook(String bookCode, GeneralBookUploadRequest generalBookUploadRequest) {

        Book book = findByBookId(bookCode);

        book.setAuthor(generalBookUploadRequest.author());
        book.setEdition(generalBookUploadRequest.edition());
        book.setYearPublished(generalBookUploadRequest.yearPublished());
        book.setPrice(generalBookUploadRequest.amount());
        book.setTitle(generalBookUploadRequest.title());
        book.setSummary(generalBookUploadRequest.summary());
        book.setLicence(generalBookUploadRequest.licence());
        book.setNoOfPages(generalBookUploadRequest.noOfPages());
    }

    @Override
    public List<ShortBook> findAll(Integer page) {
        try{
            return
                    bookRepository.findAll(PageRequest.of(page-1, 20))
                            .stream()
                            .map(book -> ShortBook.builder()
                                    .title(book.getTitle())
                                    .code(book.getBookCode())
                                    .summary(book.getSummary())
                                    .build())
                            .toList();
        }catch (NullPointerException exception) {
            throw new ResourceNotFoundException("Invalid page " + page);
        }

    }

    @Override
    public void deactivateBook(String bookCode) {
        findByBookId(bookCode).setBookStatus(INACTIVE);
    }


    private Book findById(String bookId) {
        return bookRepository.findByBookCode(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with code " + bookId + " not found"));
    }



    private Book buildBookObject(String yearPublished,
                                 BookStatus status,
                                 String author,
                                 BookType bookType,
                                 String edition,
                                 String bookId,
                                 String licence,
                                 int noOfPages,
                                 String summary,
                                 String title,
                                 String url,
                                 double price) {
        return
                Book.builder()
                        .url(url)
                        .bookType(bookType)
                        .bookStatus(status)
                        .title(title)
                        .summary(summary)
                        .price(price)
                        .noOfPages(noOfPages)
                        .licence(licence)
                        .edition(edition)
                        .author(author)
                        .bookCode(bookId)
                        .yearPublished(yearPublished)
                        .build();
    }
}
