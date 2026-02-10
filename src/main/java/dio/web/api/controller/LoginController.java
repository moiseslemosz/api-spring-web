package dio.web.api.controller;

import dio.web.api.dtos.Login;
import dio.web.api.dtos.Sessao;
import dio.web.api.repository.UserRepositorydb;
import dio.web.api.WebSecurity.*;
import dio.web.api.model.User;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class LoginController {
    @Autowired
    private PasswordEncoder encoder;
    @Autowired 
    private UserRepositorydb repository;

    @PostMapping("/login")
    public Sessao logar(@RequestBody Login login) {
        // X9: Avisa que a requisição chegou
        System.out.println("LOG: Tentando logar com usuário: " + login.getUsername());

        User user = repository.findByUsername(login.getUsername());
        if(user!=null){
            // Segundo X9: Avisa que achou o usuário
            System.out.println("LOG: Usuário encontrado: " + user.getUsername());

            boolean senhaValida = encoder.matches(login.getPassword(), user.getPassword());
            if(!senhaValida) { throw new RuntimeException("Senha inválida para o usuário: " + login.getUsername());}
            Sessao sessao = new Sessao();
            sessao.setLogin(user.getUsername());

            JWTObject jwtObject = new JWTObject();
            jwtObject.setIssuedAt(new Date(System.currentTimeMillis()));
            jwtObject.setExpiration((new Date(System.currentTimeMillis() + SecurityConfig.EXPIRATION)));
            jwtObject.setRoles(user.getRoles());
            sessao.setToken(JWTCreator.create(SecurityConfig.PREFIX, SecurityConfig.KEY, jwtObject));
            return sessao;
        } else {
            // Terceiro X9: Avisa que não achou no banco
            System.out.println("LOG: ERRO - Usuário não encontrado no banco!");
            throw new RuntimeException("Usuário não encontrado: " + login.getUsername());
        }
    }
}
