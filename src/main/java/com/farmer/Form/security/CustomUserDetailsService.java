package com.farmer.Form.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.farmer.Form.Entity.User;
import com.farmer.Form.Entity.UserStatus;
import com.farmer.Form.exception.UserNotApprovedException;
import com.farmer.Form.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
 
    private final UserRepository userRepository;
 
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (user.getStatus() != UserStatus.APPROVED) {
            throw new UserNotApprovedException("Your account is not yet approved by admin.");
        }
        return new CustomUserDetails(user);
    }
}
 
