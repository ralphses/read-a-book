package online.contactraphael.readabook.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.SEQUENCE;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(generator = "user_id_generator", strategy = SEQUENCE)
    @SequenceGenerator(name = "user_id_generator", allocationSize = 1)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Boolean isAccountNonLocked;
    private Boolean isEnabled;
}
