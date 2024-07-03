package com.venuehub.venueservice.configuration;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@Profile("test")
public class SecurityConfigTest {

    private final CustomAuthenticationException customAuthenticationException;

    @Autowired
    public SecurityConfigTest(CustomAuthenticationException customAuthenticationException) {
        this.customAuthenticationException = customAuthenticationException;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/venue/v3/**").permitAll()
                        .requestMatchers("/v2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/venue/**").permitAll()
                        .requestMatchers(new RegexRequestMatcher("/venue/\\d+/image-0", "GET")).permitAll()
                        .requestMatchers(HttpMethod.GET, "/venue").permitAll()
                        .requestMatchers(HttpMethod.POST, "/venue/**").authenticated()
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

