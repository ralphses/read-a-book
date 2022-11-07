package online.contactraphael.readabook.service.serviceImpl;

import lombok.RequiredArgsConstructor;
import online.contactraphael.readabook.model.WishList;
import online.contactraphael.readabook.model.book.Book;
import online.contactraphael.readabook.repository.WishListRepository;
import online.contactraphael.readabook.service.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;
    private final BookService bookService;

    public void addBook(String bookCode, HttpServletRequest httpServletRequest) {

        getBooks(httpServletRequest.getRemoteAddr())
                .add(bookService.findByBookId(bookCode));

    }

    public void removeBook(String bookCode, HttpServletRequest httpServletRequest) {

        getBooks(httpServletRequest.getRemoteAddr())
                .remove(bookService.findByBookId(bookCode));
    }

    public Set<Book> getBooks(String userAddress) {
        return wishListRepository.findByUserAddress(userAddress)
                .orElse(wishListRepository.save(WishList.builder()
                        .userAddress(userAddress)
                        .books(Set.of())
                        .createdAt(Instant.now())
                        .build())).getBooks();
    }

    public void clearList(HttpServletRequest httpServletRequest) {
        wishListRepository
                .findByUserAddress(httpServletRequest.getRemoteAddr())
                .ifPresent(wishListRepository::delete);
    }
}
