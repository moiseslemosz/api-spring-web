package dio.web.api.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {
    @GetMapping
    public String welcome() {
        return "Welcome to My First Web API!";
    }
    @GetMapping("/users-teste")
    @PreAuthorize("hasAnyRole('USERS', 'MANAGERS')")
    public String users() {
        return "Authorized access for USERS and MANAGERS!";
    }
    @GetMapping("/managers")
    @PreAuthorize("hasRole('MANAGERS')")
    public String managers() {
        return "Authorized access for MANAGERS only!";
    }
}
