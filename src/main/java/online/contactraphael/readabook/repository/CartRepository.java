package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    @Query(value = "SELECT cart FROM Cart cart WHERE cart.userAddress = ?1")
    Optional<Cart> findByUserAddress (String userAddress);
}
