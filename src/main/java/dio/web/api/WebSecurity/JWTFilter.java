package dio.web.api.WebSecurity;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Obtém o token da requisição com o nome do header (Authorization)
        String token = request.getHeader(JWTCreator.HEADER_AUTHORIZATION);

        try {
            if (token != null && !token.isEmpty()) {
                
                // Valida e converte o token em um Objeto Java (JWTObject)
                JWTObject tokenObject = JWTCreator.create(token, SecurityConfig.PREFIX, SecurityConfig.KEY);

                // Transforma as Roles (String) em Authorities do Spring Security
                List<SimpleGrantedAuthority> authorities = authorities(tokenObject.getRoles());

                // Monta o objeto de autenticação padrão do Spring
                UsernamePasswordAuthenticationToken userToken =
                        new UsernamePasswordAuthenticationToken(
                                tokenObject.getSubject(),
                                null,
                                authorities);

                // Define no contexto do Spring que esse usuário está LOGADO
                SecurityContextHolder.getContext().setAuthentication(userToken);

            } else {
                // Se não tem token, limpa o contexto (garante que é anônimo)
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            // MUDANÇA CRUCIAL AQUI:
            // Se der erro no token (expirado, malformado), a gente NÃO devolve erro 403 aqui.
            // A gente apenas ignora o token e limpa o contexto.
            // Assim, a requisição continua como ANÔNIMA.
            // Se a rota for pública (/login), vai passar. Se for privada (/users), o Spring barra depois.
            e.printStackTrace();
            SecurityContextHolder.clearContext();
        }

        // O filterChain.doFilter TEM que rodar sempre, com ou sem sucesso no token.
        // Isso permite que a requisição chegue ao Controller.
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para converter List<String> em List<SimpleGrantedAuthority>
    private List<SimpleGrantedAuthority> authorities(List<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}