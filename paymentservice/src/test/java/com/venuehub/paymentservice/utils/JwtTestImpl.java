package com.venuehub.paymentservice.utils;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JwtTestImpl {
    @Autowired
    private RSAKeyProperties keys;

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }
    public JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet());
        return new NimbusJwtEncoder(jwkSource);
    }
    public JWKSet jwkSet() {

        RSAKey rsaKey = new RSAKey.Builder(keys.getPublicKey())
                .privateKey(keys.getPrivateKey())
                .keyID(UUID.randomUUID().toString())
                .build();

        return new JWKSet(rsaKey);

    }
    public String generateJwt(String username, String role) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .issuer("self")
                .subject(username)
                .claim("roles", role)
                .claim("loggedInAs", "USER")
                .build();
        JwtEncoder encoder = jwtEncoder();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateJwt(String username, String role, String loggedInAs) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .issuer("self")
                .subject(username)
                .claim("roles", role)
                .claim("loggedInAs", loggedInAs)
                .build();
        JwtEncoder encoder = jwtEncoder();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
