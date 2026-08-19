package com.carrental.security;

import com.carrental.entity.User;
import com.carrental.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        String idTrimmed = identifier != null ? identifier.trim() : "";
        User user = userRepository.findByEmailIgnoreCase(idTrimmed)
                .or(() -> userRepository.findByMobileNumber(idTrimmed))
                .or(() -> userRepository.findByEmailOrMobileNumber(idTrimmed))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email or mobile number: " + identifier));

        return new UserPrincipal(user);
    }

    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return new UserPrincipal(user);
    }
}
