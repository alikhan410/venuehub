package com.venuehub.authservice.controller;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.venuehub.authservice.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
public class LoginController {

    @Autowired
    private final JWKSet jwkSet;

    @Autowired
    private LoginController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getPublicKey() throws KeySourceException {
        return jwkSet.toJSONObject();

    }
    @GetMapping("/current-user")
    public UserDto getUser(@AuthenticationPrincipal Jwt jwt) {
         return new UserDto(jwt.getSubject(), jwt.getClaimAsStringList("roles"));

    }
}
