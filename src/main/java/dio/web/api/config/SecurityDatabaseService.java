package dio.web.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import dio.web.api.repository.UserRepositorydb;
import dio.web.api.model.User;

@Service
public class SecurityDatabaseService implements UserDetailsService {
    @Autowired
    private UserRepositorydb userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {
        User userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        userEntity.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        });

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
            userEntity.getUsername(),
            userEntity.getPassword(),
            authorities
        );
        return userDetails;
    }
}