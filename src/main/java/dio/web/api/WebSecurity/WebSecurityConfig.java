package dio.web.api.WebSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    // 1. CONFIGURAÇÃO DE FILTROS (Rotas e permissões)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/users").hasAnyRole("USERS", "MANAGERS") // Exemplo: Só USER acessa /users
                .requestMatchers("/managers").hasAnyRole("MANAGERS")
                .anyRequest().authenticated()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**")) // Disable CSRF for H2 console
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())) // Allow frames for H2 console
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
    /* 2. CONFIGURAÇÃO DO ENCODER 
    /Ao definir este Bean, o Spring automaticamente o usa para checar as senhas 
    vindas do SecurityDatabaseService. */
    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt para senhas seguras
        // return NoOpPasswordEncoder.getInstance(); // Para testes sem criptografia (não recomendado
    }
}