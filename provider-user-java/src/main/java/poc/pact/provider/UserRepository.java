package poc.pact.provider;

import java.util.Optional;

public interface UserRepository {
    Optional<UserResponse> findById(Long id);

    void save(UserResponse user);
}
