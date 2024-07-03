package com.venuehub.authservice.service;

import com.venuehub.authservice.response.LoginResponse;
import com.venuehub.authservice.dto.UserDto;
import com.venuehub.authservice.model.Role;
import com.venuehub.authservice.model.User;
import com.venuehub.authservice.repository.RoleRepository;
import com.venuehub.authservice.repository.UserRepository;
import com.venuehub.commons.exception.DuplicateEntryException;
import com.venuehub.commons.exception.NoSuchUserException;
import com.venuehub.commons.exception.WrongPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    public User registerUser(UserDto body) {

        if (roleRepository.findByAuthority("USER").isEmpty()) return null;

        if (userRepository.findByEmail(body.email()).isPresent()) {
            throw new DuplicateEntryException("Email is already taken");
        }

        if (userRepository.findByUsername(body.username()).isPresent()) {
            throw new DuplicateEntryException("Username is already taken");
        }


        String encodedPassword = passwordEncoder.encode(body.password());
        Role userRole = roleRepository.findByAuthority("USER").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);

        User newUser = new User();
        newUser.setFirstName(body.firstName());
        newUser.setLastName(body.lastName());
        newUser.setEmail(body.email());
        newUser.setPassword(encodedPassword);
        newUser.setUsername(body.username());
        newUser.setAuthorities(authorities);

        return userRepository.save(newUser);

    }

    public User registerVendor(UserDto body) {

        if (roleRepository.findByAuthority("VENDOR").isEmpty()) return null;
        if (roleRepository.findByAuthority("USER").isEmpty()) return null;

        if (userRepository.findByEmail(body.email()).isPresent()) {
            throw new DuplicateEntryException("Email is already taken");
        }

        if (userRepository.findByUsername(body.username()).isPresent()) {
            throw new DuplicateEntryException("Username is already taken");
        }

        String encodedPassword = passwordEncoder.encode(body.password());
        Role vendorRole = roleRepository.findByAuthority("VENDOR").get();
        Role userRole = roleRepository.findByAuthority("USER").get();

        Set<Role> authorities = new HashSet<>();
        authorities.add(userRole);
        authorities.add(vendorRole);

        User newUser = new User();
        newUser.setFirstName(body.firstName());
        newUser.setLastName(body.lastName());
        newUser.setEmail(body.email());
        newUser.setPassword(encodedPassword);
        newUser.setUsername(body.username());
        newUser.setAuthorities(authorities);

        return userRepository.save(newUser);

    }

    public LoginResponse loginUser(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String jwt = tokenService.generateUserJwt(auth);
            return new LoginResponse(user.getUsername(), jwt);

        } catch (Exception e) {
            throw new WrongPasswordException();
        }
    }

    public LoginResponse loginVendor(String username, String password) {
        User user = userRepository.findByUsername(username).orElseThrow(NoSuchUserException::new);
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            String jwt = tokenService.generateVendorJwt(auth);
            return new LoginResponse(user.getUsername(), jwt);

        } catch (Exception e) {
            throw new WrongPasswordException();
        }
    }
}