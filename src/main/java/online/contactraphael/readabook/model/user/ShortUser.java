package online.contactraphael.readabook.model.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShortUser {

    private Long id;
    private String fullName;
    private String email;
    private UserRole userRole;
    private Boolean isAccountNonLocked;
}
