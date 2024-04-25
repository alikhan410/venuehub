package com.venuehub.authservice.service;

import com.venuehub.authservice.dto.LoginDto;
import com.venuehub.authservice.model.Role;
import com.venuehub.authservice.model.User;
import com.venuehub.authservice.repository.RoleRepository;
import com.venuehub.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Autowired
    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public User registerUser(String username, String password, String email, String firstName, String lastName) {

        if (roleRepository.findByAuthority("USER").isEmpty()) return null;

        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority("USER").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        newUser.setUsername(username);
        newUser.setAuthorities(authorities);

        return userRepository.save(newUser);

    }
    public User registerVendor(String username, String password, String email, String firstName, String lastName) {

        if (roleRepository.findByAuthority("VENDOR").isEmpty()) return null;
        if (roleRepository.findByAuthority("USER").isEmpty()) return null;

        String encodedPassword = passwordEncoder.encode(password);
        Role vendorRole = roleRepository.findByAuthority("VENDOR").get();
        Role userRole = roleRepository.findByAuthority("USER").get();

        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        authorities.add(vendorRole);

        User newUser = new User();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(encodedPassword);
        newUser.setUsername(username);
        newUser.setAuthorities(authorities);

        return userRepository.save(newUser);

    }

    public LoginDto login(String username, String password) {

        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));

            String jwt = tokenService.generateJwt(auth);
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new LoginDto(user.getUsername(), jwt);

        } catch (AuthenticationException e) {
            return new LoginDto(null, "");
        }
    }
}