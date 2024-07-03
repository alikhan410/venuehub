package com.venuehub.authservice.configuration;


import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.venuehub.authservice.utils.RSAKeyProperties;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;


import java.util.Objects;
import java.util.UUID;

@Configuration
//@EnableWebSecurity
public class Config {
    private final RSAKeyProperties keys;

    private final CustomAuthenticationException customAuthenticationException;

    private final RedissonClient redissonClient;

    @Autowired
    public Config(RSAKeyProperties rsaKeyProperties, CustomAuthenticationException customAuthenticationException, RedissonClient redissonClient) {
        this.keys = rsaKeyProperties;
        this.customAuthenticationException = customAuthenticationException;
        this.redissonClient = redissonClient;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        return new RedissonSpringCacheManager(redissonClient);
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService) {

        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();

        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);


    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/auth/v3/**").permitAll()
                        .requestMatchers("/v2/**").permitAll()
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/vendor/logout").authenticated()
                        .requestMatchers("/user/**").permitAll()
                        .requestMatchers("/vendor/**").permitAll()
                        .requestMatchers("/.well-known/jwks.json").permitAll()
//                        .requestMatchers("/login/swagger-ui/index.html").permitAll()
                        .anyRequest().authenticated()

                )
                .oauth2ResourceServer(oauth ->
                        oauth.jwt(j ->
                                        jwtAuthenticationConverter()
                                )
                                .authenticationEntryPoint(customAuthenticationException)
                );

        return http.build();

    }

    @Bean
    public JWKSet jwkSet() {

        RSAKey rsaKey = new RSAKey.Builder(keys.getPublicKey())
                .privateKey(keys.getPrivateKey())
                .keyID(UUID.randomUUID().toString())
                .build();

        return new JWKSet(rsaKey);

    }


    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(keys.getPublicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(jwkSet());
        return new NimbusJwtEncoder(jwkSource);
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
