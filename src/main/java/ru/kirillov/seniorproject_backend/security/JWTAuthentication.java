package ru.kirillov.seniorproject_backend.security;

import lombok.Data;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import ru.kirillov.seniorproject_backend.entity.UserEntity;
import ru.kirillov.seniorproject_backend.enums.Role;

import java.util.Collection;
import java.util.Set;

@Data
public class JWTAuthentication implements Authentication {

    private boolean authenticated;
    private UserEntity userEntity;
    private String firstName;
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userEntity;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return firstName;
    }
}
