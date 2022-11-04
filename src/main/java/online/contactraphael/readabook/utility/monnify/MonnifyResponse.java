package online.contactraphael.readabook.utility.monnify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonnifyResponse {
    private boolean requestSuccessful;
    private String responseMessage;
    private Integer responseCode;
    private Object responseBody;
}
