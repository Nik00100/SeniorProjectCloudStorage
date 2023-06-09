package ru.kirillov.seniorproject_backend.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import ru.kirillov.seniorproject_backend.config.AuthenticationConfigConstants;
import ru.kirillov.seniorproject_backend.entity.UserEntity;
import ru.kirillov.seniorproject_backend.enums.Role;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends GenericFilterBean {

    private final JWTProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = getTokenFromRequest((HttpServletRequest) servletRequest);
        if (token != null && jwtProvider.validateAccessToken(token)) {

            Claims claims = jwtProvider.getAccessClaims(token);

            JWTAuthentication jwtInfoToken = new JWTAuthentication();
            jwtInfoToken.setRoles(getRoles(claims));
            jwtInfoToken.setUserEntity(getUserEntityFromClaims(claims));
            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private UserEntity getUserEntityFromClaims(Claims claims) {
        String login = claims.getSubject();
        String userID = claims.getId();

        UserEntity user = new UserEntity();
        user.setId(Long.parseLong(userID));
        user.setLogin(login);

        return user;
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(AuthenticationConfigConstants.AUTH_TOKEN);
        if (StringUtils.hasText(bearer) && bearer.startsWith(AuthenticationConfigConstants.TOKEN_PREFIX)) {
            return bearer.substring(7);
        }
        return null;
    }

    private static Set<Role> getRoles(Claims claims) {
        List<String> roles = claims.get("roles", List.class);
        return roles.stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }
}
