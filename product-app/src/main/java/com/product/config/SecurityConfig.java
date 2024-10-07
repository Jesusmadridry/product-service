package com.product.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static com.product.config.RoleConstant.ROLE_ADMIN;
import static com.product.config.RoleConstant.ROLE_COMPANY_OWNER;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties({UserProperties.class})
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserProperties userProperties;

    @Bean
    public MapReactiveUserDetailsService users() {
        UserDetails[] registerUsers = userProperties.getUsers().stream()
                .map(user -> User.builder()
                        .username(user.getName())
                        .password("{noop}"+user.getPassword())
                        .roles(user.getRoles().toArray(String[]::new))
                        .build())
                .toArray(UserDetails[]::new);
        return new MapReactiveUserDetailsService(registerUsers);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers("/product/**").hasAnyRole(ROLE_ADMIN, ROLE_COMPANY_OWNER)
                                .matchers(EndpointRequest.toAnyEndpoint()).permitAll()  //Actuator endpoints
                )
                .httpBasic(httpBasicSpec -> {})
                .build();
    }
}
