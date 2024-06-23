package com.venuehub.bookingservice.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
//@EnableMethodSecurity
@Profile("dev")
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationException customAuthenticationException;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
//                        .requestMatchers(HttpMethod.GET, "/bookings/venue/\\d+").permitAll()
                        .requestMatchers(new RegexRequestMatcher("/bookings/venue/\\d+", "GET")).permitAll()
                        .requestMatchers(new RegexRequestMatcher("/bookings/venue/\\d+", "POST")).authenticated()
                        .requestMatchers(new RegexRequestMatcher("/bookings/\\d+", "PUT")).authenticated()
                        .requestMatchers(new RegexRequestMatcher("/bookings/\\d+", "DELETE")).authenticated()
                        .requestMatchers(new RegexRequestMatcher("/bookings/\\d+", "GET")).authenticated()
                        .requestMatchers(HttpMethod.GET, "/bookings/user").authenticated()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(j -> jwtAuthenticationConverter())
                        .authenticationEntryPoint(customAuthenticationException));

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtConverter;
    }
}
