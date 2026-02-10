package dio.web.api.WebSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; 
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                // 1. ROTAS PÚBLICAS
                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/login").permitAll()

                // 2. REGRAS DE DELETE (Só Gerente/admin )
                // Se alguém tentar deletar /users/1, o Spring verifica se é MANAGER primeiro
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("MANAGERS")

                // 3. REGRAS DE CRIAÇÃO/EDIÇÃO Só Gerente/admin
                .requestMatchers(HttpMethod.POST, "/users").hasRole("MANAGERS")
                .requestMatchers(HttpMethod.PUT, "/users").hasRole("MANAGERS")

                // 4. REGRA DE LEITURA (Usuários e Gerentes)
                // Se chegou aqui, é porque não é DELETE nem POST. Então libera o GET para ambos.
                .requestMatchers(HttpMethod.GET, "/users/**").hasAnyRole("USERS", "MANAGERS")
                
                // 5. BLOQUEIA O RESTO
                .requestMatchers("/managers").hasAnyRole("MANAGERS")
                .anyRequest().authenticated()
            )
            //.httpBasic(Customizer.withDefaults())
            // Ele roda DEPOIS da verificação de senha padrão, validando o Token
            .addFilterAfter(new JWTFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}