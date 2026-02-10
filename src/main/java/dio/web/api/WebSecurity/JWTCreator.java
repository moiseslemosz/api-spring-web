package dio.web.api.WebSecurity;

import java.nio.charset.StandardCharsets; // IMPORT NOVO
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey; // IMPORT NOVO

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys; // IMPORT NOVO

public class JWTCreator {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String ROLES_AUTHORITIES = "authorities";

    // Método 1: Gera o Token (Objeto -> String)
    public static String create(String prefix, String key, JWTObject jwtObject) {
        // CORREÇÃO: Transforma a senha (String) em uma Chave Criptográfica (Bytes)
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setSubject(jwtObject.getSubject())
                .setIssuedAt(jwtObject.getIssuedAt())
                .setExpiration(jwtObject.getExpiration())
                .claim(ROLES_AUTHORITIES, checkRoles(jwtObject.getRoles()))
                .signWith(secretKey, SignatureAlgorithm.HS512) // Usa a chave correta
                .compact();
        return prefix + " " + token;
    }

    // Método 2: Lê o Token (String -> Objeto)
    public static JWTObject create(String token, String prefix, String key)
            throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        
        // CORREÇÃO: Recria a chave para ler o token
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        JWTObject jwtObject = new JWTObject();
        token = token.replace(prefix, ""); 
        
        // CORREÇÃO: Usa o parserBuilder (jeito novo) em vez de parser()
        Claims claim = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        jwtObject.setSubject(claim.getSubject());
        jwtObject.setExpiration(claim.getExpiration());
        jwtObject.setIssuedAt(claim.getIssuedAt());
        jwtObject.setRoles((List) claim.get(ROLES_AUTHORITIES));
        
        return jwtObject;
    }

    private static List<String> checkRoles(List<String> roles) {
        return roles.stream()
                .map(s -> "ROLE_".concat(s.replaceAll("ROLE_", "")))
                .collect(Collectors.toList());
    }
}