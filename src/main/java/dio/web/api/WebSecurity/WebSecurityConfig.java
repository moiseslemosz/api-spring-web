package dio.web.api.WebSecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    // 1. CONFIGURAÇÃO DE FILTROS (Equivalente ao antigo configure(HttpSecurity http))
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/users").hasAnyRole("USERS", "MANAGERS") // Exemplo: Só USER acessa /users
                .requestMatchers("/managers").hasAnyRole("MANAGERS")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .formLogin(Customizer.withDefaults());

        return http.build();
    }

    // 2. CONFIGURAÇÃO DE USUÁRIOS 
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password("{noop}user123") // {noop} significa sem criptografia para a senha
            .roles("USERS")
            .build();

        UserDetails admin = User.builder()
            .username("admin")
            .password("{noop}master123")
            .roles("MANAGERS")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}