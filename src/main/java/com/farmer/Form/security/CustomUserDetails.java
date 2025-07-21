package com.farmer.Form.security;

import java.util.Collection;
import java.util.Collections;
 
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
 
import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.UserStatus;
 
import lombok.AllArgsConstructor;
 
@AllArgsConstructor
 
public class CustomUserDetails implements UserDetails {
    /**
     *
     */
 
    private static final long serialVersionUID = 1L;
 
    private final User user;
 
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
 
    @Override
    public String getPassword() {
        return user.getPassword();
    }
 
    @Override
    public String getUsername() {
        return user.getEmail(); // Using email as username
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        // Only allow login if user status is APPROVED
        return user.getStatus() == UserStatus.APPROVED;
    }
}
 
