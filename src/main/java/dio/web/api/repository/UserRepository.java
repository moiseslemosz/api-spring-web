package dio.web.api.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import dio.web.api.model.Usuario;
import dio.web.api.handler.CampoObrigatorioException;

@Repository
public class UserRepository {
    public void save(Usuario usuario) {
       if (usuario.getLogin() == null )
            throw new CampoObrigatorioException("login");
       if (usuario.getPassword() == null )
            throw new CampoObrigatorioException("password");
       if (usuario.getId() == null) {
            System.out.println("Save - Recebendo o usuário no banco de dados...");
        } else {
            System.out.println("UPDATE - Atualizando o usuário no banco de dados...");
        };
        System.out.println(usuario);
    }
    public void deleteById(Integer id) {
        System.out.printf("DELETE/id - Deletando o usuário id: %d do banco de dados...", id);
    }
    public List<Usuario> findAll() {
        System.out.println("LIST - Listando os usuários do sistema");
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario("joaozinho", "1234"));
        usuarios.add(new Usuario("mariazinha", "4321")); 
        return usuarios;
    }
    public Usuario findById(Integer id) {
        System.out.println(String.format("FIND/id - Encontrando o usuário id: %d do banco de dados...", id));
        return new Usuario("joaozinho", "1234");
    }
    public Usuario findByUsername(String username) {
        System.out.println(String.format("FIND/username - Encontrando o usuário username: %s do banco de dados...", username));
        return new Usuario("joaozinho", "1234");
    }
}
