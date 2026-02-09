package dio.web.api.WebSecurity;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
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
            
            // Continua a requisição para o Controller
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            // Se o token der erro (ex: expirou), responde com erro 403 e NÃO deixa passar
            e.printStackTrace();
            response.setStatus(HttpStatus.FORBIDDEN.value());
            // return; // O return implícito aqui impede que o filterChain rode abaixo
        }
    }

    // Método auxiliar para converter List<String> em List<SimpleGrantedAuthority>
    private List<SimpleGrantedAuthority> authorities(List<String> roles) {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }
}