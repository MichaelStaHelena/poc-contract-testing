package poc.pact.provider;

public record UserResponse(Long id, String fullName, String email, boolean active) {
}
