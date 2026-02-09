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
    // Injetando o repositório de usuários para acessar o banco de dados
    @Autowired
    private UserRepositorydb repository;
    
    //criptografia de senha
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Este método é executado na inicialização da aplicação, garantindo que os usuários admin e user existam no banco de dados.
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
