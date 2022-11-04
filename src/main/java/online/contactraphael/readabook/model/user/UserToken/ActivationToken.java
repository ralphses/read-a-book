package online.contactraphael.readabook.model.user.UserToken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ActivationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String token;
    private Boolean isUsed;

    @Enumerated(EnumType.STRING)
    private TokenPurpose purpose;
    private Instant createdAt;
    private Instant expiresAt;
}
