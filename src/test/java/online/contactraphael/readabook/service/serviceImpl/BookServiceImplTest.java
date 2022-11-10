package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.book.BookStatus;
import online.contactraphael.readabook.model.book.BookType;
import online.contactraphael.readabook.model.dtos.GeneralBookUploadRequest;
import online.contactraphael.readabook.model.dtos.NewBookUploadRequest;
import online.contactraphael.readabook.model.dtos.ShortBook;
import online.contactraphael.readabook.model.payment.PaymentStatus;
import online.contactraphael.readabook.model.payment.Transactions;
import online.contactraphael.readabook.model.user.AppUser;
import online.contactraphael.readabook.model.user.UserRole;
import online.contactraphael.readabook.repository.AppUserRepository;
import online.contactraphael.readabook.repository.BookRepository;
import online.contactraphael.readabook.repository.BookUploaderRepository;
import online.contactraphael.readabook.service.service.TransactionsService;
import online.contactraphael.readabook.utility.uploads.FileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl underTest;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private ServletRequestAttributes attributes;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private TransactionsService transactionsService;
    @Mock
    private BookUploaderRepository bookUploaderRepository;
    @Mock
    private FileManager fileManager;

    @Mock
    private MultipartFile file;


    private final String EMAIL = "eze.raph@gmail.com";
    private AppUser appUser;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {


        appUser = AppUser.builder()
                .userRole(UserRole.CUSTOMER)
                .isAccountNonLocked(true)
                .password(passwordEncoder.encode("password"))
                .isEnabled(true)
                .fullName("Raphael")
                .build();

        Mockito.when(appUserRepository.findByEmail(EMAIL))
                .thenReturn(Optional.of(appUser));

    }

    @Test
    public void itShouldAddBookWIthValidParams() {

        NewBookUploadRequest newBookUploadRequest = new NewBookUploadRequest(
                20,
                "licence",
                "2002",
                "author",
                "title",
                "summary",
                "edition",
                300
        );

        String expected = underTest.addNewBook(EMAIL, newBookUploadRequest);
        System.out.println("expected = " + expected);

    }

    @Test
    public void itShouldAddBookWIthInvalidEmail() {

        NewBookUploadRequest newBookUploadRequest = new NewBookUploadRequest(
                20,
                "licence",
                "2002",
                "author",
                "title",
                "summary",
                "edition",
                300
        );
        underTest.addNewBook("EMAIL", newBookUploadRequest);

    }

    @Test
    public void itShouldUploadMultipartFileWithInValidParamsBookNotActive() {

        String reference = "REFERENCE";
        String id = "ID";

        Book book = Book.builder()
                .price(300.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode(id)
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.APPROVED)
                .url("url")
                .id(1L)
                .build();

        Transactions transactions = Transactions.builder()
                .transactionReference(reference)
                .ownerEmail(EMAIL)
                .bookId(id)
                .paymentStatus(PaymentStatus.PAID)
                .createdAt(Instant.now())
                .paymentMethod("TRANSFER")
                .amountPaid(300.0)
                .id(1L)
                .build();
        Mockito.when(transactionsService.findByReference(reference))
                .thenReturn(transactions);

        Mockito.when(bookRepository.findByBookCode(id)).thenReturn(Optional.of(book));

        assertThrows(UnauthorizedUserException.class, () ->underTest.upload(reference, file, id));

    }

    @Test
    public void itShouldUploadMultipartFileWithInValidParamsPaymentNotPaid() {

        String reference = "REFERENCE";
        String id = "ID";

        Book book = Book.builder()
                .price(300.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode(id)
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.APPROVED)
                .url("url")
                .id(1L)
                .build();

        Transactions transactions = Transactions.builder()
                .transactionReference(reference)
                .ownerEmail(EMAIL)
                .bookId(id)
                .paymentStatus(PaymentStatus.CANCELLED)
                .createdAt(Instant.now())
                .paymentMethod("TRANSFER")
                .amountPaid(300.0)
                .id(1L)
                .build();
        Mockito.when(transactionsService.findByReference(reference))
                .thenReturn(transactions);

        Mockito.when(bookRepository.findByBookCode(id)).thenReturn(Optional.of(book));

        assertThrows(UnauthorizedUserException.class, () ->underTest.upload(reference, file, id));

    }

    @Test
    public void itShouldUploadMultipartFileWithValidParamsAndIsWaved() {

        String reference = "COUPON_REFERENCE";
        String id = "ID";

        Book book = Book.builder()
                .price(0.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode(id)
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.APPROVED)
                .url("url")
                .id(1L)
                .build();

        Transactions transactions = Transactions.builder()
                .transactionReference(reference)
                .ownerEmail(EMAIL)
                .bookId(id)
                .paymentStatus(PaymentStatus.CANCELLED)
                .createdAt(Instant.now())
                .paymentMethod("NONE")
                .amountPaid(0.0)
                .id(1L)
                .build();
        Mockito.when(transactionsService.findByReference(reference))
                .thenReturn(transactions);

        Mockito.when(bookRepository.findByBookCode(id)).thenReturn(Optional.of(book));

        assertThrows(UnauthorizedUserException.class, () ->underTest.upload(reference, file, id));

    }

    @Test
    public void itShouldUploadMultipartFileWithValidParamsAndIsWavedWithBookCodeNull() {

        String reference = "COUPON_REFERENCE";
        String id = "ID";

        Book book = Book.builder()
                .price(0.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode(id)
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.APPROVED)
                .url("url")
                .id(1L)
                .build();

        Transactions transactions = Transactions.builder()
                .transactionReference(reference)
                .ownerEmail(EMAIL)
                .bookId(id)
                .paymentStatus(PaymentStatus.CANCELLED)
                .createdAt(Instant.now())
                .paymentMethod("NONE")
                .amountPaid(0.0)
                .id(1L)
                .build();
        Mockito.when(transactionsService.findByReference(reference))
                .thenReturn(transactions);

        Mockito.when(bookRepository.findByBookCode(id)).thenReturn(Optional.of(book));

        assertThrows(UnauthorizedUserException.class, () ->underTest.upload(reference, file, null));

    }

    @Test
    public void itShouldUploadMultipartFileWithValidParams() {

        String reference = "REFERENCE";
        String id = "ID";

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode(id)
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();

        Transactions transactions = Transactions.builder()
                .transactionReference(reference)
                .ownerEmail(EMAIL)
                .bookId(id)
                .paymentStatus(PaymentStatus.PAID)
                .createdAt(Instant.now())
                .paymentMethod("NONE")
                .amountPaid(20.0)
                .id(1L)
                .build();
        Mockito.when(transactionsService.findByReference(reference))
                .thenReturn(transactions);

        Mockito.when(bookRepository.findByBookCode(id)).thenReturn(Optional.of(book));

//        underTest.upload(reference, file, id);

    }

    @Test
    public void itShouldFindBookById() {

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode("id")
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();
        Mockito.when(bookRepository.findByBookCode("id")).thenReturn(Optional.of(book));
        Book b = underTest.findByBookId("id");
        assertEquals(b.getBookCode(), "id");

    }

    @Test
    public void itShouldFindBookByIdWIthInvalidId() {

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode("id")
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();
        Mockito.when(bookRepository.findByBookCode("id1")).thenReturn(Optional.of(book));

        assertThrows(ResourceNotFoundException.class, () -> underTest.findByBookId("is"));

    }

    @Test
    public void itShouldDeactivateBookWithInValidParams() {

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode("id")
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();
        Mockito.when(bookRepository.findByBookCode("id1")).thenReturn(Optional.of(book));

        assertThrows(ResourceNotFoundException.class, () -> underTest.deactivateBook("is"));

    }

    @Test
    public void itShouldDeactivateBookWithValidParams() {

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode("id")
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();
        Mockito.when(bookRepository.findByBookCode("id")).thenReturn(Optional.of(book));
        underTest.deactivateBook("id");
    }

    @Test
    public void itShouldFindAllBooks() {

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode("id")
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> shortBooks = new PageImpl<>(List.of(book));


        Mockito.when(bookRepository.findAll(pageable))
               .thenReturn(shortBooks);

        assertNotNull(bookRepository.findAll(pageable));
        List<ShortBook> all = underTest.findAll(1);
        assertEquals(all.get(0).getCode(), book.getBookCode());
    }

    @Test
    public void itShouldFindAllBooksWithInvalidPage() {

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode("id")
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Book> shortBooks = new PageImpl<>(List.of(book));


        Mockito.when(bookRepository.findAll(pageable))
                .thenReturn(shortBooks);

        assertNotNull(bookRepository.findAll(pageable));
        assertThrows(ResourceNotFoundException.class, () -> underTest.findAll(2));
    }

    @Test
    public void itShouldUpdateBook() {
        GeneralBookUploadRequest generalBookUploadRequest = new GeneralBookUploadRequest(
                20,
                "licence",
                "2002",
                "author",
                "title",
                "summary",
                "edition",
                "name",
                "email@gmail.com",
                0.0
        );

        Book book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode("id")
                .author("author")
                .edition("edition")
                .licence("licence")
                .noOfPages(20)
                .summary("summary")
                .title("title")
                .bookStatus(BookStatus.ACTIVE)
                .url("url")
                .id(1L)
                .build();
        Mockito.when(bookRepository.findByBookCode("id"))
                .thenReturn(Optional.of(book));

        assertDoesNotThrow(() -> underTest.updateBook("id", generalBookUploadRequest));
        ;
    }

    @Test
    public void itShouldAddNewGeneralBookWithValidParams() {
        GeneralBookUploadRequest generalBookUploadRequest = new GeneralBookUploadRequest(
                20,
                "licence",
                "2002",
                "author",
                "title",
                "summary",
                "edition",
                "name",
                "email@gmail.com",
                0.0
        );
        underTest.addNewBook(generalBookUploadRequest);
    }

}