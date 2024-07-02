package com.venuehub.venueservice.utils;

import com.venuehub.commons.exception.UserForbiddenException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

public class SecurityChecks {
    public static void vendorCheck(Jwt jwt) {

        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("VENDOR")) {
            throw new UserForbiddenException();
        }

    }
}
