package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.user.UserToken.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    @Query(value = "SELECT auth FROM ActivationToken auth WHERE auth.email = ?1")
    Optional<ActivationToken> findByEmail(String email);

    @Query(value = "SELECT auth FROM ActivationToken auth WHERE auth.token = ?1")
    Optional<ActivationToken> findByToken(String token);
}
