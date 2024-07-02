package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.LoginDto;
import com.venuehub.authservice.dto.UserDto;
import com.venuehub.authservice.dto.RegisterUserDto;
import com.venuehub.authservice.model.User;
import com.venuehub.authservice.service.AuthenticationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final AuthenticationService authenticationService;

    @Autowired
    public UserController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/user/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody @Valid RegisterUserDto body) {

        User newUser = authenticationService.registerUser(body);

        UserDto loginUser = authenticationService.loginUser(newUser.getUsername(), body.password());

        return new ResponseEntity<>(loginUser, HttpStatus.CREATED);
    }

    @PostMapping("/user/login")
    public ResponseEntity<UserDto> loginUser(@RequestBody @Valid LoginDto body) {

        UserDto loginUser = authenticationService.loginUser(body.username(), body.password());

        return new ResponseEntity<>(loginUser, HttpStatus.OK);
    }

    @GetMapping("/user/logout")
    public ResponseEntity<UserDto> logoutUser(@AuthenticationPrincipal Jwt jwt) {
        UserDto loginUser = new UserDto(jwt.getSubject(), null);

        return new ResponseEntity<>(loginUser, HttpStatus.OK);
    }

}
