package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    @Query(value = "SELECT wishList FROM WishList wishList WHERE wishList.userAddress = ?1")
    Optional<WishList> findByUserAddress(String userAddress);
}
