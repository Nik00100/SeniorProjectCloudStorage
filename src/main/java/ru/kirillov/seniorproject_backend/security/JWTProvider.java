package ru.kirillov.seniorproject_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kirillov.seniorproject_backend.config.AuthenticationConfigConstants;
import ru.kirillov.seniorproject_backend.entity.UserEntity;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
public class JWTProvider {

    private final SecretKey secret;
    private final int tokenLifetime;
    private final Set<String> setTokens = new HashSet<>();

    public JWTProvider(@Value("${jwt.secret.access}") String secret, @Value("${TOKEN_LIFETIME}") int tokenLifetime) {
        this.secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.tokenLifetime = tokenLifetime;
    }

    public String generateToken(@NonNull UserEntity userEntity) throws IllegalArgumentException {
        Date now = new Date();
        Date exp = Date.from(LocalDateTime.now().plusMinutes(tokenLifetime)
                .atZone(ZoneId.systemDefault()).toInstant());

        String token = Jwts.builder()
                .setId(String.valueOf(userEntity.getId()))
                .setSubject(userEntity.getLogin())
                .setIssuedAt(now)
                .setNotBefore(now)
                .setExpiration(exp)
                .signWith(secret)
                .claim("roles", userEntity.getRoles())
                .compact();
        setTokens.add(token);
        return token;
    }

    public boolean validateAccessToken(@NonNull String token) {
        boolean isTokenInList = setTokens.contains(token);
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return isTokenInList;
        } catch (ExpiredJwtException expEx) {
            log.error("Срок действия токена истек (Token expired)", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Форма токена не поддерживается jwt (Unsupported jwt)", unsEx);
        } catch (MalformedJwtException mjEx) {
            log.error("Форма токена некорректна для jwt (Malformed jwt)", mjEx);
        } catch (SignatureException sEx) {
            log.error("Недействительная подпись (Invalid signature)", sEx);
        } catch (Exception e) {
            log.error("Недопустимый токен (Invalid token)", e);
        }
        return false;
    }

    public Claims getAccessClaims(@NonNull String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return claimsJws.getBody();
        } catch (Exception e) {
            log.error("Недопустимый токен", e);
            throw new IllegalArgumentException("Недопустимый токен");
        }
    }

    public void removeToken(String token) {
        setTokens.remove(token.substring(AuthenticationConfigConstants.TOKEN_PREFIX.length()));
    }
}
