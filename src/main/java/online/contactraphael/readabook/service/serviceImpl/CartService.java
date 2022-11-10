package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.cart.CartBook;
import online.contactraphael.readabook.model.cart.Cart;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.dtos.ShortBook;
import online.contactraphael.readabook.model.dtos.ShortCartBook;
import online.contactraphael.readabook.model.dtos.monnify.NewMonnifyPaymentRequest;
import online.contactraphael.readabook.repository.CartBookRepository;
import online.contactraphael.readabook.repository.CartRepository;
import online.contactraphael.readabook.service.service.BookService;
import online.contactraphael.readabook.service.service.PaymentService;
import online.contactraphael.readabook.service.service.TransactionsService;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static online.contactraphael.readabook.configuration.main.AppConfig.PAYMENT_REDIRECT_URL;
import static online.contactraphael.readabook.utility.monnify.MonnifyConfig.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final BookService bookService;
    private final CartBookRepository cartBookRepository;
    private final PaymentService paymentService;
    private final TransactionsService transactionsService;

    public void addBook(String bookCode, String quantity, HttpServletRequest httpServletRequest) {

        try {
            int qty = Integer.parseInt(quantity);
            String remoteAddress = httpServletRequest.getRemoteAddr();

            Book book = bookService.findByBookId(bookCode);
            double currentAmount = book.getPrice() * qty;

            Optional<Cart> cartOptional = cartRepository.findByUserAddress(remoteAddress);
            CartBook cartBook = CartBook.builder().book(book).quantity(qty).build();

            if(cartOptional.isPresent()) {

                Cart cart = cartOptional.get();

                Optional<CartBook> existingCartBook =
                        cart.getBooks().stream()
                        .filter(thisCartBook -> thisCartBook.getBook().getBookCode().equals(book.getBookCode()))
                        .findFirst();

                if(existingCartBook.isPresent()) {
                    existingCartBook.get().setQuantity(existingCartBook.get().getQuantity() + qty);
                }
                else {
                    cartBookRepository.save(cartBook);
                    cart.getBooks().add(cartBook);
                }
                cart.setTotalAmount(cart.getTotalAmount() + currentAmount);

            }
            else {
                cartBookRepository.save(cartBook);

                Cart cart = Cart.builder()
                        .books(Set.of(cartBook))
                        .totalAmount(currentAmount)
                        .createdAt(Instant.now())
                        .userAddress(remoteAddress)
                        .build();

                cartRepository.save(cart);
            }

        } catch (NumberFormatException exception) {
            throw new InvalidRequestParamException("invalid quantity " + quantity);
        }
    }

    public Set<ShortCartBook> getCartBooks(HttpServletRequest httpServletRequest) {

        Set<ShortCartBook> shortCartBooks = new HashSet<>();

        getCartByAddress(httpServletRequest).getBooks().forEach(cartBook -> {
            Book book = cartBook.getBook();
            shortCartBooks.add(ShortCartBook.builder()
                            .quantity(cartBook.getQuantity())
                            .book(ShortBook.builder()
                                    .code(book.getBookCode())
                                    .title(book.getTitle())
                                    .build())
                    .build());
        });
        return shortCartBooks;
    }

    public void removeBookFromCart(String bookCode, HttpServletRequest httpServletRequest) {
        CartBook cartBookByBookCode = getCartBookByBookCode(bookCode);

        Cart cart = getCartByAddress(httpServletRequest);

        cart.setTotalAmount(cart.getTotalAmount() - cartBookByBookCode.getQuantity() * cartBookByBookCode.getBook().getPrice());
        cart.getBooks().remove(cartBookByBookCode);

        cartBookRepository.delete(cartBookByBookCode);
    }

    public void setNewBookQty(String bookCode, int newQty, HttpServletRequest httpServletRequest) {

        Cart cart = getCartByAddress(httpServletRequest);
        cart.getBooks().stream()
                .filter(b -> b.getBook().getBookCode().equals(bookCode))
                .findFirst()
                .ifPresent(cartBook -> {

                    cartBook.setQuantity(newQty);
                    cart.setTotalAmount(getNewTotalAmount(cart));

                    if(newQty <= 0) {
                        cartBookRepository.delete(cartBook);
                    }
                });
    }

    public InitPaymentResponse checkout(String email, HttpServletRequest httpServletRequest) {

        Cart cart = getCartByAddress(httpServletRequest);

        if(cart.getBooks().isEmpty()) {
            throw new InvalidRequestParamException("cart empty");
        }

        NewMonnifyPaymentRequest newMonnifyPaymentRequest = NewMonnifyPaymentRequest.builder()
                .redirectUrl(PAYMENT_REDIRECT_URL)
                .customerName(email)
                .paymentReference(UUID.randomUUID().toString().replace("-", ""))
                .paymentMethods(ALLOWED_PAYMENT_METHODS)
                .paymentDescription("Book purchase")
                .customerEmail(email)
                .currencyCode(NGN_CURRENCY_CODE)
                .contractCode(CONTRACT_CODE)
                .amount(cart.getTotalAmount())
                .build();

        InitPaymentResponse initPaymentResponse = paymentService.initializeNewMonnifyPayment(newMonnifyPaymentRequest);

        transactionsService.newPayment(
                "",
                email,
                initPaymentResponse.getPaymentReference(),
                initPaymentResponse.getTransactionReference(),
                cart.getTotalAmount(),
                null,
                "PENDING",
                "Book purchase fee");

        return initPaymentResponse;
    }

    private CartBook getCartBookByBookCode(String bookCode) {
        return cartBookRepository
                .findByBookCode(bookCode)
                .orElseThrow(() -> new InvalidRequestParamException("Invalid book code " + bookCode));
    }

    private Double getNewTotalAmount(Cart cart) {
        return cart.getBooks().stream()
                .map(b -> b.getBook().getPrice() * b.getQuantity())
                .reduce(Double::sum)
                .orElse(0.0);
    }

    private Cart getCartByAddress(HttpServletRequest httpServletRequest) {
        return cartRepository
                .findByUserAddress(httpServletRequest.getRemoteAddr())
                .orElseThrow(() -> new ResourceNotFoundException("Cart Empty"));
    }

    public void clearCart(HttpServletRequest httpServletRequest) {
        cartRepository
                .findByUserAddress(httpServletRequest.getRemoteAddr())
                .ifPresent(cartRepository::delete);
    }

    public void clearCartPerDay() {
        cartRepository.findAll().stream()
                .filter(cart -> cart.getCreatedAt().plus(1, ChronoUnit.DAYS).isBefore(Instant.now()))
                .forEach(cartRepository::delete);
    }
}
