package online.contactraphael.readabook.repository;

import online.contactraphael.readabook.model.user.UserToken.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByPasswordHash(String passwordHash);
}
