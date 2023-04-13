package dev.fastball.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.portal.context.PortalContext;
import dev.fastball.portal.controller.PortalController;
import dev.fastball.portal.security.filter.JwtAuthenticationFilter;
import dev.fastball.portal.security.filter.PasswordAuthenticationFilter;
import dev.fastball.portal.security.handler.UnloginAuthenticationEntryPoint;
import dev.fastball.portal.security.service.DefaultUserDetailsService;
import dev.fastball.portal.security.utils.JwtUtils;
import dev.fastball.portal.security.utils.ResponseUtils;
import dev.fastball.portal.service.FastballPortalService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AutoConfiguration
@EnableConfigurationProperties(FastballJwtProperties.class)
@ComponentScan(basePackages = "dev.fastball.security.auth")
public class FastballSecurityConfiguration {

    private final String[] PATH_RELEASE = {"/api/login"};

    @Bean
    public PortalController portalController(FastballPortalService portalService) {
        return new PortalController(portalService);
    }

    @Bean
    public PortalContext portalContext() {
        return new PortalContext();
    }


    @Bean
    public UserDetailsService userDetailsService(FastballPortalService fastballPortalService) {
        return new DefaultUserDetailsService(fastballPortalService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtUtils jwtUtils(FastballJwtProperties jwtProperties) {
        return new JwtUtils(jwtProperties);
    }

    @Bean
    public ResponseUtils jsonUtils(ObjectMapper objectMapper) {
        return new ResponseUtils(objectMapper);
    }

    @Bean
    public PasswordAuthenticationFilter passwordAuthenticationFilter(AuthenticationManager authenticationManager, ResponseUtils responseUtils, JwtUtils jwtUtils) {
        return new PasswordAuthenticationFilter(authenticationManager, responseUtils, jwtUtils);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        return new JwtAuthenticationFilter(authenticationManager, jwtUtils);
    }


    @Bean
    public UnloginAuthenticationEntryPoint unloginAuthenticationEntryPoint(ResponseUtils responseUtils) {
        return new UnloginAuthenticationEntryPoint(responseUtils);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, PasswordAuthenticationFilter passwordAuthenticationFilter, JwtAuthenticationFilter jwtAuthenticationFilter, UnloginAuthenticationEntryPoint unloginAuthenticationEntryPoint) throws Exception {
        http.cors().and().csrf().disable();
        http.authorizeHttpRequests((auth) -> auth.antMatchers(PATH_RELEASE).permitAll().anyRequest().authenticated());
        http.addFilterBefore(passwordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).addFilter(jwtAuthenticationFilter);
        http.exceptionHandling().authenticationEntryPoint(unloginAuthenticationEntryPoint);
        http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return http.build();
    }

}
