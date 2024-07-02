package com.venuehub.bookingservice.utils;

import com.venuehub.commons.exception.UserForbiddenException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

public class SecurityChecks {
    public static void userCheck(Jwt jwt) {
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("USER") || !jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException();
        }

    }

    public static void userCheck(Jwt jwt, String message) {
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("USER") || !jwt.getClaim("loggedInAs").equals("USER")) {
            throw new UserForbiddenException(message);
        }

    }

    public static void vendorCheck(Jwt jwt) {
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("VENDOR") || !jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }

    }

    public static void vendorCheck(Jwt jwt, String message) {
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        if (!roles.contains("VENDOR") || !jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException(message);
        }

    }

}
