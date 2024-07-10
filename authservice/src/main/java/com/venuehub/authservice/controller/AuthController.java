package com.venuehub.authservice.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.venuehub.authservice.dto.CurrentUserDto;
import com.venuehub.commons.exception.UserForbiddenException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Tag(name = "Auth Info", description = "Exposes public key and info about current-user")
@RestController
public class AuthController implements AuthApi {

    @Autowired
    private final JWKSet jwkSet;

    @Autowired
    public AuthController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

    @GetMapping("/.well-known/jwks.json")
    public Map<String,Object> getPublicKey() {
        return jwkSet.toJSONObject();
    }

    @GetMapping("/current-user")
    public CurrentUserDto getUser(@AuthenticationPrincipal Jwt jwt) {

        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("USER")) {
            throw new UserForbiddenException();
        }
        return new CurrentUserDto(jwt.getSubject(), roles, jwt.getClaim("loggedInAs"));

    }
}
