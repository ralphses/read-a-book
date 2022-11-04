package online.contactraphael.readabook.utility;

import online.contactraphael.readabook.exception.InvalidRequestParamException;
import online.contactraphael.readabook.exception.ResourceNotFoundException;
import online.contactraphael.readabook.exception.UnauthorizedUserException;
import online.contactraphael.readabook.exception.UnsuccessfulRequestException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

@Component
public class CustomWebClient {


    public <T> Object sendRequest(String baseUrl,
                                  String uri,
                                  HttpMethod method,
                                  Object requestBody,
                                  MultiValueMap<String, String> headers,
                                  MediaType mediaType,
                                  Class<T> type) {

        if(requestBody != null) {
            try {
                return
                        WebClient.create(baseUrl)
                                .method(method)
                                .uri(uri)
                                .bodyValue(requestBody)
                                .headers(header -> header.addAll(headers))
                                .accept(mediaType)
                                .retrieve()
                                .onStatus(HttpStatus::isError, getResponseSpec())
                                .bodyToMono(type)
                                .block(Duration.ofSeconds(10));
            }catch (Exception exception) {
                throw new UnsuccessfulRequestException(exception.getMessage());
            }

        }
        else {
            try {
                return
                        WebClient.create(baseUrl)
                                .method(method)
                                .uri(uri)
                                .headers(header -> header.addAll(headers))
                                .accept(mediaType)
                                .retrieve()
                                .onStatus(HttpStatus::isError, getResponseSpec())
                                .bodyToMono(type)
                                .block(Duration.ofSeconds(10));
            }catch (Exception exception) {
                throw new UnsuccessfulRequestException(exception.getMessage());
            }
        }
    }

    private Function<ClientResponse, Mono<? extends Throwable>> getResponseSpec() {

        return response -> switch (response.rawStatusCode()) {
            case 400 -> Mono.error(new InvalidRequestParamException("Bad request made "));
            case 401 -> Mono.error(new UnauthorizedUserException("Illegal User"));
            case 403 -> Mono.error(new UnauthorizedUserException("Forbidden"));
            case 404 -> Mono.error(new ResourceNotFoundException("Not found"));
            default -> Mono.error(new UnsuccessfulRequestException("Server temporary down"));

        };
    }

}
