package com.venuehub.authservice.controller;

import com.venuehub.authservice.dto.LoginDto;
import com.venuehub.authservice.dto.Mapper;
import com.venuehub.authservice.dto.UserDto;
import com.venuehub.authservice.model.User;
import com.venuehub.authservice.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class VendorController {
    private final AuthenticationService authenticationService;
    @Autowired
    private VendorController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/vendor/register")
    public ResponseEntity<UserDto> registerVendor(@RequestBody User body) {

        User newUser = authenticationService.registerVendor(
                body.getUsername(),
                body.getPassword(),
                body.getEmail(),
                body.getFirstName(),
                body.getLastName()
        );



        Mapper mapper = new Mapper();
        UserDto userDto = mapper.modelToDto(newUser);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);

    }

    @PostMapping("/vendor/login")
    @ResponseBody
    public ResponseEntity<LoginDto> loginVendor(@RequestBody User body) {

        LoginDto loginVendor = authenticationService.login(body.getUsername(), body.getPassword());

        return new ResponseEntity<>(loginVendor, HttpStatus.OK);
    }
}
