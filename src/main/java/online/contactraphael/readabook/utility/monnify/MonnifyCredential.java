package online.contactraphael.readabook.utility.monnify;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import online.contactraphael.readabook.utility.CustomWebClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.Base64;

import static online.contactraphael.readabook.utility.monnify.MonnifyConfig.LOGIN_URL;
import static online.contactraphael.readabook.utility.monnify.MonnifyConfig.MONNIFY_BASE_URL;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Getter
@Slf4j
@Setter
@RequiredArgsConstructor
@EnableScheduling
@ConfigurationProperties(prefix = "monnify")
public class MonnifyCredential {

    private final CustomWebClient customWebClient;

    private String key;
    private String secrete;
    private String accessToken;

    private String base64Secrete() {
        return MonnifyConfig.BASIC_AUTHORIZATION_PREFIX +
                Base64.getEncoder().encodeToString((getKey()+":"+getSecrete()).getBytes());
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 2500000)
    public void setAccessToken() {

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", base64Secrete());

        MonnifyResponse response = (MonnifyResponse) customWebClient.sendRequest(
                MONNIFY_BASE_URL,
                LOGIN_URL,
                POST,
                null,
                headers,
                APPLICATION_JSON,
                MonnifyResponse.class);

        this.accessToken =  new ObjectMapper()
                .convertValue(response.getResponseBody(), AccessTokenResponse.class).getAccessToken();

        log.info("New Authentication set at {}", LocalDateTime.now());
    }


}
