package poc.pact.provider;

public record UserResponse(Long id, String name, String email, boolean active) {
}
