package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    @Query(value = "SELECT authToken FROM AuthToken authToken WHERE authToken.remoteAddress = ?1")
    Optional<AuthToken> findByRemoteAddress(String remoteAddress);

    @Query(value = "SELECT authToken FROM AuthToken authToken WHERE authToken.subject = ?1")
    Optional<AuthToken> findBySubject(String subject);

    @Query(value = "SELECT authToken FROM AuthToken authToken WHERE authToken.token = ?1")
    Optional<AuthToken> findByToken(String token);
}
