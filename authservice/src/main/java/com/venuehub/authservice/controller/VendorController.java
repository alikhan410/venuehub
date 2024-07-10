package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.LoginDto;
import com.venuehub.authservice.response.LoginResponse;
import com.venuehub.authservice.dto.UserDto;
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

//@Api(value = "Vendor Controller", tags = {"Vendor Login Api"})
@Tag(name = "Vendor Login", description = "Let's vendor register/login/logout")
@RestController
@Validated
public class VendorController implements VendorApi {
    private final AuthenticationService authenticationService;

    @Autowired
    public VendorController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/vendor/register")
    public ResponseEntity<LoginResponse> registerVendor(@RequestBody @Valid UserDto body) {

        return new ResponseEntity<>(authenticationService.registerVendor(body), HttpStatus.CREATED);
    }

    @PostMapping("/vendor/login")
    public ResponseEntity<LoginResponse> loginVendor(@RequestBody @Valid LoginDto body) {

        LoginResponse loginVendor = authenticationService.loginVendor(body.username(), body.password());

        return new ResponseEntity<>(loginVendor, HttpStatus.OK);
    }

    @GetMapping("/vendor/logout")
    public ResponseEntity<LoginResponse> logoutVendor(@AuthenticationPrincipal Jwt jwt) {

        LoginResponse loginVendor = new LoginResponse(jwt.getSubject(), null);

        return new ResponseEntity<>(loginVendor, HttpStatus.OK);
    }
}
