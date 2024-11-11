package com.np.mcdiffy.utilities;

import com.np.mcdiffy.entities.UserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtility {
    private final String secret = "test1234test1234test1234test12t1234test1234test1234";
    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    public String generateJwtToken(UserDetails user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("Roles",
                        user.getRoles())
                .signWith(secretKey)
                .expiration(new Date(new Date().getTime()+300000000))
                .issuedAt(new Date())
                .issuer("np")
                .compact();
    }

    public boolean validateToken(String jwt) {
       return Jwts.parser().
               verifyWith(secretKey).
               build().
               parseSignedClaims(jwt).
               getPayload().getExpiration().after(new Date());
    }

    public String extractUserName(String jwt) {
        return Jwts.parser().
                verifyWith(secretKey).
                build().
                parseSignedClaims(jwt).
                getPayload().getSubject();
    }
}
