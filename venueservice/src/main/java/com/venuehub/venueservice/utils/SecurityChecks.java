package com.venuehub.venueservice.utils;

import com.venuehub.commons.exception.UserForbiddenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

public class SecurityChecks {
    public static final Logger logger = LoggerFactory.getLogger(SecurityChecks.class);

    public static void vendorCheck(Jwt jwt) {
        String rolesString = jwt.getClaim("roles");
        List<String> roles = Arrays.stream(rolesString.split(" ")).toList();

        logger.info("Performing security checks for the user: {}, loggedInAs: {} with roles {}", jwt.getSubject(), jwt.getClaim("loggedInAs"), roles);

        if (!jwt.getClaim("loggedInAs").equals("VENDOR")) {
            throw new UserForbiddenException();
        }

        if (!roles.contains("VENDOR")) {
            throw new UserForbiddenException();
        }

    }
}
