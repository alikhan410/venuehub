package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.LoginDto;
import com.venuehub.authservice.dto.UserDto;
import com.venuehub.authservice.dto.RegisterUserDto;
import com.venuehub.authservice.model.User;
import com.venuehub.authservice.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//@Api(value = "Vendor Controller", tags = {"Vendor Login Api"})
@RestController
@Validated
public class VendorController {
    private final AuthenticationService authenticationService;

    @Autowired
    public VendorController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/vendor/register")
    public ResponseEntity<UserDto> registerVendor(@RequestBody @Valid RegisterUserDto body) {

        User newUser = authenticationService.registerUser(
                body.username(),
                body.password(),
                body.email(),
                body.firstName(),
                body.lastName()
        );

        UserDto loginVendor = authenticationService.loginVendor(newUser.getUsername(), newUser.getPassword());

        return new ResponseEntity<>(loginVendor, HttpStatus.CREATED);

    }

//    @ApiOperation(value = "Logs in a vendor and returns a JWT key", notes = "Provide username and password to log in and receive a JWT token.")
    @PostMapping("/vendor/login")
    public ResponseEntity<UserDto> loginVendor(@RequestBody @Valid LoginDto body) {

        UserDto loginVendor = authenticationService.loginVendor(body.username(), body.password());

        return new ResponseEntity<>(loginVendor, HttpStatus.OK);
    }

    @GetMapping("/vendor/logout")
    public ResponseEntity<UserDto> logoutVendor(@AuthenticationPrincipal Jwt jwt) {

        UserDto loginVendor = new UserDto(jwt.getSubject(), null);

        return new ResponseEntity<>(loginVendor, HttpStatus.OK);
    }
}
