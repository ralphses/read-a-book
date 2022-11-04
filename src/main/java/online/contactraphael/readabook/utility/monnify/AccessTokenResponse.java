package online.contactraphael.readabook.utility.monnify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AccessTokenResponse {
    private String accessToken;
    private Integer expiresIn;

}
