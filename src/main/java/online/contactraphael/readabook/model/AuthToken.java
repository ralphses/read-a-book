package online.contactraphael.readabook.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.time.Instant;

import static javax.persistence.GenerationType.AUTO;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken {

    @Id
    @GeneratedValue(strategy = AUTO)
    private Long id;

    @Column(length = 600)
    private String token;
    private String remoteAddress;
    private String subject;
    private Boolean isLoggedIn;
    private Boolean isValid;
    private Instant generatedAt;
    private Instant expiresAt;

}
