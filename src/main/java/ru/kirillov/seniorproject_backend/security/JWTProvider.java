package ru.kirillov.seniorproject_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.kirillov.seniorproject_backend.config.AuthenticationConfigConstants;
import ru.kirillov.seniorproject_backend.entity.UserEntity;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JWTProvider {

    private final SecretKey secret;
    private final int tokenLifetime;
    private final List<String> listTokens = new ArrayList<>();
    //private UserEntity userEntity;

    public JWTProvider(@Value("${jwt.secret.access}") String secret, @Value("${TOKEN_LIFETIME}") int tokenLifetime) {
        this.secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.tokenLifetime = tokenLifetime;
    }

//    public UserEntity getAuthenticatedUser() {
//        return userEntity;
//    }

    public String generateToken(@NonNull UserEntity userEntity) throws IllegalArgumentException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
                .claim("roles",
                        authentication.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .compact();

        listTokens.add(token);
        //this.userEntity = userEntity;
        return token;
    }

    public boolean validateAccessToken(@NonNull String token) {
        for (String t : listTokens) {
            if (!t.equals(token)) {
                return false;
            }
        }
        return validateToken(token, secret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Срок действия токена истек (Token expired)", expEx);
        } catch (UnsupportedJwtException unsEx) {
            log.error("Форма токена неподдерживается jwt (Unsupported jwt)", unsEx);
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
        return getClaims(token, secret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void removeToken(String token) {
        listTokens.remove(token.substring(AuthenticationConfigConstants.TOKEN_PREFIX.length()));
    }
}
