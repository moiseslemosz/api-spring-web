package dio.web.api.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dio.web.api.model.User;
import dio.web.api.repository.UserRepositorydb;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class StartApplication implements CommandLineRunner {
    @Autowired
    private UserRepositorydb repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        User user = repository.findByUsername("admin");
        if (user == null) {
            user = new User();
            user.setName("Administrator");
            user.setUsername("admin");
            user.setPassword(passwordEncoder.encode("master123"));
            user.getRoles().add("MANAGERS");
            repository.save(user);
        }
        user = repository.findByUsername("user");
        if (user == null) {
            user = new User();
            user.setName("User");
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.getRoles().add("USERS");
            repository.save(user);
        }
    }
}
