package poc.pact.order;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderUserControllerTest {
    @Test
    void exposesTheOrderSpecificView() {
        // The HTTP collaboration itself is covered by UserClientContractTest.
        assertThat(new OrderUser(123L, "Michael").name()).isEqualTo("Michael");
    }
}
