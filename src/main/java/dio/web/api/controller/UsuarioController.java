package dio.web.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import dio.web.api.model.User; 
import dio.web.api.repository.UserRepositorydb;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/users")
public class UsuarioController {

    // INJEÇÃO DO REPOSITÓRIO DO BANCO
    @Autowired
    private UserRepositorydb repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<User> list() { // Retorna lista de User (Entidade)
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
    public void postUser(@RequestBody User user) { // Recebe User no corpo
        repository.save(user);
    }

    @PutMapping
    public void putUser(@RequestBody User user) {
        repository.save(user);
    }
}