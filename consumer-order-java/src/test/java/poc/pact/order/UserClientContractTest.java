package poc.pact.order;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "user-service", providerType = ProviderType.SYNCH)
class UserClientContractTest {
    @Pact(consumer = "order-service", provider = "user-service")
    V4Pact userExists(PactDslWithProvider builder) {
        PactDslJsonBody body = new PactDslJsonBody()
                .integerType("id", 123)
                .stringType("name", "Michael");

        return builder
                .given("user 123 exists")
                .uponReceiving("a request for user 123 from order-service")
                .path("/users/123")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(body)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "userExists")
    void getsOnlyTheFieldsNeededByOrderService(MockServer mockServer) {
        OrderUser user = new UserClient(mockServer.getUrl()).getUser(123);

        assertThat(user).isEqualTo(new OrderUser(123L, "Michael"));
    }
}
