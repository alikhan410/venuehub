package com.venuehub.bookingservice.configuration;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
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
//                        .requestMatchers(HttpMethod.GET, "/bookings/venue/\\d+").permitAll()
                        .requestMatchers("/booking/v3/api-docs").permitAll()
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
