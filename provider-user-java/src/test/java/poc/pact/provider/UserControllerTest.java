package poc.pact.provider;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest {
    private final UserRepository userRepository = new InMemoryUserRepository();
    private final UserController controller = new UserController(userRepository);

    @Test
    void returnsTheBaselineUser() {
        userRepository.save(new UserResponse(123L, "Michael", "michael@email.com", true));

        UserResponse response = controller.findById(123L);

        assertThat(response.id()).isEqualTo(123L);
        assertThat(response.fullName()).isEqualTo("Michael");
        assertThat(response.email()).isEqualTo("michael@email.com");
        assertThat(response.active()).isTrue();
    }
}
