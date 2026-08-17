package poc.pact.order;

import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

public class UserClient {
    private final RestClient client;

    public UserClient(String baseUrl) {
        this.client = RestClient.builder().baseUrl(baseUrl).build();
    }

    public OrderUser getUser(long id) {
        return client.get()
                .uri("/users/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(OrderUser.class);
    }
}
