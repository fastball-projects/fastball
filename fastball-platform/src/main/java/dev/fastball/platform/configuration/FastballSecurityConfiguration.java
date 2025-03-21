package dev.fastball.platform.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fastball.core.component.runtime.ComponentRegistry;
import dev.fastball.platform.context.PortalContext;
import dev.fastball.platform.security.filter.JwtAuthenticationFilter;
import dev.fastball.platform.security.filter.PasswordAuthenticationFilter;
import dev.fastball.platform.security.handler.FastballAuthenticationFailureHandler;
import dev.fastball.platform.security.handler.FastballAuthenticationSuccessHandler;
import dev.fastball.platform.security.handler.UnloginAuthenticationEntryPoint;
import dev.fastball.platform.security.matcher.DynamicAnonymousPathRequestMatcher;
import dev.fastball.platform.security.service.DefaultUserDetailsService;
import dev.fastball.platform.security.utils.JwtUtils;
import dev.fastball.platform.security.utils.ResponseUtils;
import dev.fastball.platform.service.PlatformUserService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
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
@EnableConfigurationProperties({FastballSecurityProperties.class})
@ComponentScan(basePackages = "dev.fastball.platform.core")
public class FastballSecurityConfiguration {

    private final String[] PATH_RELEASE = {"/login", "/api/login"};

    @Bean
    public PortalContext fastballPlatformContext() {
        return new PortalContext();
    }

    @Bean
    public UserDetailsService fastballUserDetailsService(PlatformUserService fastballPlatformService) {
        return new DefaultUserDetailsService(fastballPlatformService);
    }

    @Bean
    public PasswordEncoder fastballPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager fastballAuthenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtUtils fastballJwtUtils(FastballSecurityProperties securityProperties) {
        FastballJwtProperties jwtProperties = securityProperties.getJwt();
        if (jwtProperties == null) {
            jwtProperties = new FastballJwtProperties();
        }
        return new JwtUtils(jwtProperties);
    }

    @Bean
    public ResponseUtils fastballJsonUtils(ObjectMapper objectMapper) {
        return new ResponseUtils(objectMapper);
    }

    @Bean
    public PasswordAuthenticationFilter fastballPasswordAuthenticationFilter(AuthenticationManager authenticationManager, ResponseUtils responseUtils, FastballAuthenticationSuccessHandler fastballAuthenticationSuccessHandler, FastballAuthenticationFailureHandler fastballAuthenticationFailureHandler) {
        return new PasswordAuthenticationFilter(authenticationManager, responseUtils, fastballAuthenticationSuccessHandler, fastballAuthenticationFailureHandler);
    }

    @Bean
    public FastballAuthenticationSuccessHandler fastballAuthenticationSuccessHandler(ResponseUtils responseUtils, JwtUtils jwtUtils) {
        return new FastballAuthenticationSuccessHandler(responseUtils, jwtUtils);
    }

    @Bean
    public FastballAuthenticationFailureHandler fastballAuthenticationFailureHandler(ResponseUtils responseUtils) {
        return new FastballAuthenticationFailureHandler(responseUtils);
    }

    @Bean
    public JwtAuthenticationFilter fastballJwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        return new JwtAuthenticationFilter(authenticationManager, jwtUtils);
    }


    @Bean
    public UnloginAuthenticationEntryPoint fastballUnloginAuthenticationEntryPoint(ResponseUtils responseUtils) {
        return new UnloginAuthenticationEntryPoint(responseUtils);
    }

    @Bean
    public SecurityFilterChain fastballFilterChain(HttpSecurity http, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, PasswordAuthenticationFilter passwordAuthenticationFilter, JwtAuthenticationFilter jwtAuthenticationFilter, UnloginAuthenticationEntryPoint unloginAuthenticationEntryPoint, ComponentRegistry componentRegistry, FastballSecurityProperties portalProperties) throws Exception {
        DynamicAnonymousPathRequestMatcher anonymousPathRequestMatcher = new DynamicAnonymousPathRequestMatcher(componentRegistry);
        http.cors().and().csrf().disable();
        http.authorizeHttpRequests((auth) -> {
            auth.requestMatchers(
                            HttpMethod.GET,
                            "/",
                            "/*.html",
                            "/**.html",
                            "/assets/*.css",
                            "/assets/*.js",
                            "/**.css",
                            "/**.js",
                            "/*.jpg",
                            "/*.ico"
                    ).permitAll()
                    .requestMatchers(PATH_RELEASE).permitAll()
                    .requestMatchers(anonymousPathRequestMatcher).permitAll()
                    .requestMatchers(portalProperties.getAnonymousPath()).permitAll()
                    .requestMatchers("/api/**").authenticated();
        });
        http.addFilterBefore(passwordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).addFilter(jwtAuthenticationFilter);
        http.exceptionHandling().authenticationEntryPoint(unloginAuthenticationEntryPoint);
        http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return http.build();
    }

}
