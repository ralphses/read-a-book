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

@Service
@Transactional
@RequiredArgsConstructor
public class WishListService {

    private final WishListRepository wishListRepository;
    private final BookService bookService;

    public void addBook(String bookCode, HttpServletRequest httpServletRequest) {
        Book book = bookService.findByBookId(bookCode);

        String userAddress = httpServletRequest.getRemoteAddr();

        WishList wishList = wishListRepository.findByUserAddress(userAddress)
                .orElse(wishListRepository.save(WishList.builder()
                        .userAddress(userAddress)
                        .createdAt(Instant.now())
                        .build()));

        wishList.getBooks().add(book);
    }
}
