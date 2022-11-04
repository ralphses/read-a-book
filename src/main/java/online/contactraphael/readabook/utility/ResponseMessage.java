package online.contactraphael.readabook.utility;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class ResponseMessage {
    private String status;
    private int responseCode;
    private Map<String, Object> responseBody;
}
