package poc.pact.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/users")
public class OrderUserController {
    private final UserClient userClient;

    public OrderUserController(@Value("${user-service.base-url:http://localhost:8080}") String userServiceUrl) {
        this.userClient = new UserClient(userServiceUrl);
    }

    @GetMapping("/{id}")
    public OrderUser getUser(@PathVariable long id) {
        return userClient.getUser(id);
    }
}
