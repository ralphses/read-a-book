package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query(value = "SELECT user FROM AppUser user WHERE user.email = ?1")
    Optional<AppUser> findByEmail(String email);
}
