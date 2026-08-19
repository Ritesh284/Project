package com.carrental.service;

import com.carrental.dto.AuthRequest;
import com.carrental.dto.AuthResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.entity.Role;
import com.carrental.entity.User;
import com.carrental.exception.BadRequestException;
import com.carrental.repository.UserRepository;
import com.carrental.security.JwtService;
import com.carrental.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @org.springframework.beans.factory.annotation.Value("${app.admin.email:waghritesh907@gmail.com}")
    private String adminEmail;

    @org.springframework.beans.factory.annotation.Value("${app.admin.mobile:9022165093}")
    private String adminMobile;

    @org.springframework.beans.factory.annotation.Value("${app.admin.password:Ritu@123}")
    private String adminPassword;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match. Please re-enter your password.");
        }

        String email = request.getEmail().trim().toLowerCase();
        String mobile = request.getMobileNumber().trim();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with email " + email + " already exists. Please sign in instead.");
        }

        if (userRepository.existsByMobileNumber(mobile)) {
            throw new BadRequestException("An account with mobile number " + mobile + " already exists. Please sign in instead.");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setMobileNumber(mobile);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        User savedUser = userRepository.save(user);

        UserPrincipal userPrincipal = new UserPrincipal(savedUser);
        String jwtToken = jwtService.generateToken(userPrincipal);

        return new AuthResponse(
                jwtToken,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getMobileNumber(),
                savedUser.getRole().name()
        );
    }

    public AuthResponse login(AuthRequest request) {
        String identifier = request.getIdentifier() != null ? request.getIdentifier().trim() : "";
        String password = request.getPassword() != null ? request.getPassword().trim() : "";

        User user = userRepository.findByEmailIgnoreCase(identifier)
                .or(() -> userRepository.findByMobileNumber(identifier))
                .or(() -> userRepository.findByEmailOrMobileNumber(identifier))
                .orElseThrow(() -> new BadCredentialsException("Invalid email/mobile number or password"));

        boolean isAdmin = (adminEmail != null && adminEmail.equalsIgnoreCase(identifier)) ||
                          (adminMobile != null && adminMobile.equals(identifier)) ||
                          (user.getRole() == Role.ROLE_ADMIN);

        if (isAdmin) {
            // Admin password is NOT stored in database. It is authenticated directly from .env file!
            if (adminPassword == null || !adminPassword.equals(password)) {
                throw new BadCredentialsException("Invalid email/mobile number or password");
            }

            UserPrincipal userPrincipal = new UserPrincipal(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    userPrincipal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwtToken = jwtService.generateToken(userPrincipal);
            return new AuthResponse(
                    jwtToken,
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getMobileNumber(),
                    user.getRole().name()
            );
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email/mobile number or password");
        }

        UserPrincipal userPrincipal = new UserPrincipal(user);
        String jwtToken = jwtService.generateToken(userPrincipal);

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getRole().name()
        );
    }

    public User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BadRequestException("User is not authenticated. Please log in.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUser();
        }

        String username = authentication.getName();
        return userRepository.findByEmailOrMobileNumber(username)
                .orElseThrow(() -> new BadRequestException("Authenticated user not found."));
    }
}
