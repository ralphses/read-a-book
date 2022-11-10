package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.Cart;
import online.contactraphael.readabook.model.CartBook;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.book.BookStatus;
import online.contactraphael.readabook.model.book.BookType;
import online.contactraphael.readabook.model.dtos.ShortCartBook;
import online.contactraphael.readabook.model.dtos.monnify.NewMonnifyPaymentRequest;
import online.contactraphael.readabook.repository.CartBookRepository;
import online.contactraphael.readabook.repository.CartRepository;
import online.contactraphael.readabook.service.service.BookService;
import online.contactraphael.readabook.service.service.PaymentService;
import online.contactraphael.readabook.service.service.TransactionsService;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private BookService bookService;
    @Mock
    private CartBookRepository cartBookRepository;
    @Mock
    private PaymentService paymentService;
    @Mock
    private TransactionsService transactionsService;
    @Mock
    HttpServletRequest httpServletRequest;

    @InjectMocks
    private CartService underTest;

    private Book book;
    private Cart cart;
    private CartBook cartBook;
   private final String ID = "id";

    private final String USER_ADDRESS = "0.001.0";

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .price(20.0)
                .bookType(BookType.FEATURED)
                .yearPublished("2002")
                .bookCode(ID)
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


        Mockito.when(bookService.findByBookId(ID))
                .thenReturn(book);

        cartBook = CartBook.builder()
                .quantity(2)
                .book(book)

                .id(1L)
                .build();

        cart = Cart.builder()
                .books(Set.of(cartBook))
                .createdAt(Instant.now())
                .totalAmount(20.0)
                .id(1L)
                .userAddress(USER_ADDRESS)
                .build();

        Mockito.when(cartRepository.findByUserAddress(USER_ADDRESS))
                .thenReturn(Optional.of(cart));

        Mockito.when(httpServletRequest.getRemoteAddr())
                .thenReturn(USER_ADDRESS);
    }

    @Test
    public void itShouldAddABookToCartWithValidParams() {
        String bookCode = "id";
        underTest.addBook(bookCode, "2", httpServletRequest);
    }

    @Test
    public void itShouldAddABookToCartWithInValidParamsWithInvalidQuantity() {
        String bookCode = "id";
        assertThrows(InvalidRequestParamException.class, () -> underTest.addBook(bookCode, "2j", httpServletRequest));
    }

    @Test
    public void itShouldAddABookToCartWithValidParamsWhenNewCart() {

        Mockito.when(cartRepository.findByUserAddress(USER_ADDRESS))
                .thenReturn(Optional.empty());

        String bookCode = "id";
        underTest.addBook(bookCode, "2", httpServletRequest);
    }

    @Test
    public void itShouldAddABookToCartWithValidParamsWhenNewCartBook() {

        book = Book.builder()
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

        cartBook = null;

        cart = Cart.builder()
                .books(Set.of())
                .createdAt(Instant.now())
                .totalAmount(20.0)
                .id(1L)
                .userAddress(USER_ADDRESS)
                .build();

        Mockito.when(bookService.findByBookId("id2"))
                .thenReturn(book);

        String bookCode = "id2";
        underTest.addBook(bookCode, "2", httpServletRequest);
    }

    @Test
    public void itShouldGetCartBooksWithValidParams() {
        Set<ShortCartBook> cartBooks = underTest.getCartBooks(httpServletRequest);
        assertEquals(1, cartBooks.size());
    }

    @Test
    public void itShouldGetCartBooksWithInvalidParamsAndEmptyCart() {
        Mockito.when(cartRepository.findByUserAddress(USER_ADDRESS))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> underTest.getCartBooks(httpServletRequest) );
    }

    @Test
    public void itShouldSetNewBookQuantityForACartWithValidQuantity() {

        Mockito.when(cartBookRepository.findByBookCode(ID))
                        .thenReturn(Optional.of(cartBook));

        underTest.setNewBookQty(ID, 2, httpServletRequest);
    }

    @Test
    public void itShouldSetNewBookQuantityForACartWithInValidQuantity() {

        Mockito.when(cartBookRepository.findByBookCode(ID))
                .thenReturn(Optional.of(cartBook));

        underTest.setNewBookQty(ID, -1, httpServletRequest);
    }

    @Test
    public void itShouldClearCartWithValidAddress() {
        underTest.clearCart(httpServletRequest);
    }

    @Test
    public void itShouldClearCartWithInValidAddress() {
        Mockito.when(httpServletRequest.getRemoteAddr())
                        .thenReturn(null);
        underTest.clearCart(httpServletRequest);
    }

    @Test
    public void ItShouldTestClearCartPerDay() {
        Mockito.when(cartRepository.findAll())
                        .thenReturn(List.of(cart));
        underTest.clearCartPerDay();
    }

    @Test
    public void ItShouldTestClearCartPerDayWithInvalidParams() {
        Mockito.when(cartRepository.findAll())
                .thenReturn(List.of());
        underTest.clearCartPerDay();
    }

    @Test
    public void itCheckout() {

        NewMonnifyPaymentRequest newMonnifyPaymentRequest = NewMonnifyPaymentRequest.builder()
                .paymentReference("paymentReference")
                .build();

        InitPaymentResponse initPaymentResponse = new InitPaymentResponse(
                "transactionReference",
                "paymentReference",
                "merchantName",
                "apiKey",
                new String[]{"TRANSFER"},
                "redirectUrl",
                "checkoutUrl"
        );

        assertNotNull(initPaymentResponse);

        Mockito.when(paymentService.initializeNewMonnifyPayment(newMonnifyPaymentRequest))
                .thenReturn(initPaymentResponse);

        assertThrows(NullPointerException.class, () -> underTest.checkout("email@gmail.com", httpServletRequest));

    }
}