package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.CartBook;
import online.contactraphael.readabook.model.Cart;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.repository.CartBookRepository;
import online.contactraphael.readabook.repository.CartListRepository;
import online.contactraphael.readabook.service.service.BookService;
import online.contactraphael.readabook.service.service.PaymentService;
import online.contactraphael.readabook.utility.monnify.InitPaymentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class CartListService {

    private final CartListRepository cartListRepository;
    private final BookService bookService;
    private final CartBookRepository cartBookRepository;
    private final PaymentService paymentService;

    public void addBook(String bookCode, String quantity, HttpServletRequest httpServletRequest) {

        try {
            int qty = Integer.parseInt(quantity);
            String remoteAddress = httpServletRequest.getRemoteAddr();

            Book book = bookService.findByBookId(bookCode);
            double currentAmount = book.getPrice() * qty;

            Optional<Cart> wishListOptional = cartListRepository.findByUserAddress(remoteAddress);
            CartBook cartBook = CartBook.builder().book(book).quantity(qty).build();

            if(wishListOptional.isPresent()) {

                Cart cart = wishListOptional.get();

                Optional<CartBook> existingCartBook =
                        cart.getBooks().stream()
                        .filter(thisCartBook -> thisCartBook.getBook().getBookCode().equals(book.getBookCode()))
                        .findAny();

                if(existingCartBook.isPresent()) {
                    existingCartBook.get().setQuantity(existingCartBook.get().getQuantity() + qty);
                    cart.setTotalAmount(cart.getTotalAmount() + currentAmount);
                }
                else {
                    cartBookRepository.save(cartBook);

                    cart.getBooks().add(cartBook);
                    cart.setTotalAmount(cart.getTotalAmount() + currentAmount);
                }
            }
            else {
                cartBookRepository.save(cartBook);

                Cart cart = Cart.builder()
                        .books(Set.of(cartBook))
                        .totalAmount(currentAmount)
                        .createdAt(Instant.now())
                        .userAddress(remoteAddress)
                        .build();

                cartListRepository.save(cart);
            }

        } catch (NumberFormatException exception) {
            throw new InvalidRequestParamException("invalid quantity " + quantity);
        }
    }

    public Set<CartBook> getCartBooks(HttpServletRequest httpServletRequest) {
        return getCartByAddress(httpServletRequest).getBooks();
    }

    public void removeBookFromCart(String bookCode, HttpServletRequest httpServletRequest) {
        getCartByAddress(httpServletRequest)
                .getBooks()
                .remove(getCartBookByBookCode(bookCode));
    }

    public void setNewBookQty(String bookCode, int newQty, HttpServletRequest httpServletRequest) {

        getCartByAddress(httpServletRequest)
                .getBooks()
                .stream()
                .filter(b -> b.getBook().getBookCode().equals(bookCode))
                .findFirst()
                .ifPresent(cartBook -> cartBook.setQuantity(newQty));
    }

    public InitPaymentResponse checkout(String email, HttpServletRequest httpServletRequest) {
        Cart cart = getCartByAddress(httpServletRequest);
        return paymentService.initPayment(cart, email);
    }

    private CartBook getCartBookByBookCode(String bookCode) {
        return cartBookRepository
                .findByBookCode(bookCode)
                .orElseThrow(() -> new InvalidRequestParamException("Invalid book code " + bookCode));
    }

    private Cart getCartByAddress(HttpServletRequest httpServletRequest) {
        return cartListRepository
                .findByUserAddress(httpServletRequest.getRemoteAddr())
                .orElseThrow(() -> new ResourceNotFoundException("Cart Empty"));
    }

}
