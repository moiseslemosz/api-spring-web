package dio.web.api.controller;

import dio.web.api.model.User;
import dio.web.api.repository.UserRepositorydb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    @Autowired
    private UserRepositorydb repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> list() {
        return repository.findAll();
    }

    @GetMapping("/{username}")
    public User getOne(@PathVariable("username") String username) {
        return repository.findByUsername(username);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        repository.deleteById(id);
    }

    // POST: Criação de usuário novo 
    @PostMapping
    public void postUser(@RequestBody User user) {
        criptografarEBlindar(user); 
        repository.save(user);
    }

    // PUT: Atualização de usuário existente
    @PutMapping 
    public void putUser(@RequestBody User user) {
        // Lógica para não apagar a senha se ela vier nula
        User userBanco = repository.findById(user.getId()).orElse(null);

        if(userBanco != null) {
            // Se mandou nome novo, troca. Se não, mantém o velho.
            if(user.getName() != null) userBanco.setName(user.getName());

            // Se mandou senha nova, criptografa. Se não, mantém a velha.
            if(user.getPassword() != null && !user.getPassword().isEmpty()) {
                userBanco.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // Se mandou roles novas, aplica a blindagem.
            if(user.getRoles() != null) {
                // Reaproveitando a lógica de blindagem, mas aplicada ao objeto do banco
                boolean isManager = user.getRoles().contains("MANAGERS");
                userBanco.setRoles(new ArrayList<>());
                if (isManager) {
                    userBanco.getRoles().add("MANAGERS");
                } else {
                    userBanco.getRoles().add("USERS");
                }
            }

            repository.save(userBanco);
        } else {
            throw new RuntimeException("Usuário não encontrado para atualização.");
        }
    }

    // Método auxiliar usado APENAS no POST (Criação)
    private void criptografarEBlindar(User user) {
        String pass = user.getPassword();
        user.setPassword(passwordEncoder.encode(pass));

        boolean isManager = user.getRoles() != null && user.getRoles().contains("MANAGERS");
        user.setRoles(new ArrayList<>());
        
        if (isManager) {
            user.getRoles().add("MANAGERS");
        } else {
            user.getRoles().add("USERS");
        }
    }
}