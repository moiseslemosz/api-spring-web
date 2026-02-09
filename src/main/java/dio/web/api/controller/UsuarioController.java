package dio.web.api.controller;

import dio.web.api.model.User;
import dio.web.api.repository.UserRepositorydb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public void postUser(@RequestBody User user) {
        criptografarEBlindar(user); // Chama a lógica
        repository.save(user);
    }

    // O método PUT é similar ao POST, mas recebe o ID do usuário na URL para garantir que estamos editando um usuário existente.
    @PutMapping("/{id}")
    public void putUser(@PathVariable("id") Integer id, @RequestBody User user) {
        user.setId(id);
        criptografarEBlindar(user); // Reutiliza a mesma lógica
        repository.save(user);
    }

    // Método auxiliar privado para não repetir código
    private void criptografarEBlindar(User user) {
        // Criptografia de senha
        String pass = user.getPassword();
        user.setPassword(passwordEncoder.encode(pass));

        // Blindagem de Roles
        boolean isManager = user.getRoles() != null && user.getRoles().contains("MANAGERS");
        user.setRoles(new java.util.ArrayList<>());
        
        if (isManager) {
            user.getRoles().add("MANAGERS");
        } else {
            user.getRoles().add("USERS");
        }
    }
}