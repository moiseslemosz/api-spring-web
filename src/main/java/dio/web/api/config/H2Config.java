package dio.web.api.config;

// 1. O Import mudou para JakartaWebServlet
import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2Config {

    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2servletRegistration() {
        // 2. Usando a classe correta para Spring Boot 3
        ServletRegistrationBean<JakartaWebServlet> registrationBean = new ServletRegistrationBean<>(new JakartaWebServlet());
        
        // Define a URL do console
        registrationBean.addUrlMappings("/h2-console/*");
        return registrationBean;
    }
}