package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.LoginDto;
import com.venuehub.authservice.response.LoginResponse;
import com.venuehub.authservice.dto.UserDto;
import com.venuehub.authservice.model.User;
import com.venuehub.authservice.service.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


//@Api(value = "User Controller", tags = {"User Login Api"})
@Tag(name = "User Login", description = "Let's user register/login/logout")
@RestController
@Validated
public class UserController {
    public static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<LoginResponse> registerUser(@RequestBody @Valid UserDto body) {

        User newUser = authenticationService.registerUser(body);

        LoginResponse loginUser = authenticationService.loginUser(newUser.getUsername(), body.password());

        return new ResponseEntity<>(loginUser, HttpStatus.CREATED);
    }

    @PostMapping("/user/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginDto body) {

        LoginResponse loginUser = authenticationService.loginUser(body.username(), body.password());

        return new ResponseEntity<>(loginUser, HttpStatus.OK);
    }

    @GetMapping("/user/logout")
    public ResponseEntity<LoginResponse> logoutUser(@AuthenticationPrincipal Jwt jwt) {
        LoginResponse loginUser = new LoginResponse(jwt.getSubject(), null);

        return new ResponseEntity<>(loginUser, HttpStatus.OK);
    }

}
