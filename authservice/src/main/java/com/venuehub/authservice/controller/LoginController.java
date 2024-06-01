package com.venuehub.authservice.controller;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSet;
import com.venuehub.authservice.dto.CurrentUserDto;
import com.venuehub.commons.exception.UserForbiddenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


//@Api(value = "Login Controller", tags = {"Login API"})
@RestController
public class LoginController {

    @Autowired
    private final JWKSet jwkSet;

    @Autowired
    public LoginController(JWKSet jwkSet) {
        this.jwkSet = jwkSet;
    }

//    @ApiOperation(value = "Exposes the public key for authorization")

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> getPublicKey() throws KeySourceException {
        return jwkSet.toJSONObject();

    }

//    @ApiOperation(
//            value = "Returns the current authenticated user",
//            notes = "This endpoint returns the details of the currently authenticated user. " +
//                    "The user must have the 'USER' role to access this endpoint."
//    )
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
