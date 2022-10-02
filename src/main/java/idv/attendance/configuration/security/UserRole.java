package idv.attendance.configuration.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public enum UserRole {
    USER, ADMIN;

    List<? extends GrantedAuthority> toGrantedAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.name()));
    }
}
