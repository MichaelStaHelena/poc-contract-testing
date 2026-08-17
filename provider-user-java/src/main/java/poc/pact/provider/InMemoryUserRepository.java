package poc.pact.provider;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Deliberately small persistence implementation for this PoC.
 *
 * <p>It keeps the HTTP layer realistic while allowing Pact provider states to prepare data without
 * requiring external infrastructure.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, UserResponse> users = new ConcurrentHashMap<>();

    @Override
    public Optional<UserResponse> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void save(UserResponse user) {
        users.put(user.id(), user);
    }
}
