package dio.web.api.WebSecurity;

import java.util.List;
import java.util.stream.Collectors;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class JWTCreator {
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String ROLES_AUTHORITIES = "authorities";

    // Método 1: Gera o Token (Objeto -> String)
    public static String create(String prefix, String key, JWTObject jwtObject) {
        String token = Jwts.builder()
                .setSubject(jwtObject.getSubject())
                .setIssuedAt(jwtObject.getIssuedAt())
                .setExpiration(jwtObject.getExpiration())
                .claim(ROLES_AUTHORITIES, checkRoles(jwtObject.getRoles()))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
        return prefix + " " + token;
    }

    // Método 2: Lê o Token (String -> Objeto)
    // Lança exceção se o token for inválido (ex: expirou, assinatura errada, etc)
    public static JWTObject create(String token, String prefix, String key)
            throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        
        JWTObject jwtObject = new JWTObject();
        token = token.replace(prefix, ""); // Remove o "Bearer " da string
        
        Claims claim = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        
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