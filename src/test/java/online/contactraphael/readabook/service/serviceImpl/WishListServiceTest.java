package online.contactraphael.readabook.service.serviceImpl;

import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.model.WishList;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.model.book.BookStatus;
import online.contactraphael.readabook.repository.WishListRepository;
import online.contactraphael.readabook.service.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class WishListServiceTest {

    @Mock
    private WishListRepository wishListRepository;
    @Mock
    private BookService bookService;
    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private WishListService underTest;

    @Test
    public void itShouldAddBookToAWishListWithValidParams() {

        String bookCode = "id";

        Book book = Book.builder()
                .id(1L)
                .bookCode(bookCode)
                .url("url")
                .bookStatus(BookStatus.ACTIVE)
                .title("title")
                .summary("summary")
                .noOfPages(2)
                .build();

        Mockito.when(bookService.findByBookId(bookCode))
                .thenReturn(book);

        underTest.addBook(bookCode, httpServletRequest);
    }

    @Test
    public void itShouldAddBookToAWishListWithInValidParams() {
        String bookCode = "id";
        String address = "0100";

        Book book = Book.builder()
                .id(1L)
                .bookCode(bookCode)
                .url("url")
                .bookStatus(BookStatus.ACTIVE)
                .title("title")
                .summary("summary")
                .noOfPages(2)
                .build();

        WishList wishList = WishList.builder()
                .books(Set.of(book))
                .createdAt(Instant.now())
                .userAddress(address)
                .id(1L)
                .build();

        Mockito.when(bookService.findByBookId(bookCode))
                .thenReturn(book);

        Mockito.when(wishListRepository.findByUserAddress(address))
                .thenReturn(Optional.of(wishList));

        underTest.addBook(bookCode, httpServletRequest);
    }

    @Test
    public void itShouldClearWishList() {
        underTest.clearList(httpServletRequest);
    }

    @Test
    public void itShouldRemoveBookFromWishList() {

        String bookCode = "id";
        String address = "0100";

        Book book = Book.builder()
                .id(1L)
                .bookCode(bookCode)
                .url("url")
                .bookStatus(BookStatus.ACTIVE)
                .title("title")
                .summary("summary")
                .noOfPages(2)
                .build();

        Mockito.when(bookService.findByBookId(bookCode))
                .thenReturn(book);

        Mockito.when(httpServletRequest.getRemoteAddr())
                .thenReturn(address);

        underTest.removeBook(bookCode, httpServletRequest);
    }

}