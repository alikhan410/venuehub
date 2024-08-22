package com.venuehub.venueservice.configuration;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import java.util.Objects;

@Configuration
@EnableMethodSecurity
@Profile({"dev", "prod"})
public class SecurityConfig {

    private final CustomAuthorizationException customAuthenticationException;
    private final RedissonClient redissonClient;

    @Autowired
    public SecurityConfig(CustomAuthorizationException customAuthenticationException, RedissonClient redissonClient) {
        this.customAuthenticationException = customAuthenticationException;
        this.redissonClient = redissonClient;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/venue/v3/**").permitAll()
                        .requestMatchers("/v2/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/images/**").authenticated()
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
    public CacheManager cacheManager(RedissonClient redissonClient) {
        return new RedissonSpringCacheManager(redissonClient);
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
