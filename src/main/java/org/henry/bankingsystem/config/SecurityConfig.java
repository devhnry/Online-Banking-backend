package org.henry.bankingsystem.config;

import lombok.RequiredArgsConstructor;
import org.henry.bankingsystem.enums.AdminRoles;
import org.henry.bankingsystem.service.LogoutService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

//    private final JwtSecurityFilter jwtAuthFilter;
    private final LogoutService logoutService;
    private final SecurityAuthProvider authProvider;
    private final JwtSecurityFilter jwtSecurityFilter;

    /**
     * Processes incoming requests in the web application.
     * Handles authentication, authorization, and protection against exploits.
     * @param http HttpSecurity configures security for HTTP requests.
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .authorizeHttpRequests(request -> request
                    .requestMatchers("auth/**", "/error**").permitAll() // Permits all Users to access Authentication Endpoints
                        // Protects all admin Endpoints unless having an Admin Role
                    .requestMatchers("api/v1/admin/**").hasAnyAuthority(Arrays.toString(AdminRoles.values()))
                    .anyRequest().authenticated()) // Every other Request has to be authenticated.
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                /*
                    Sets the Authentication Provider to use the Custom Auth Provider Defined
                    Uses the JWT Filter Defined in the Config.
                */
                .authenticationProvider(authProvider.authenticationProvider()).addFilterBefore(
                        jwtSecurityFilter, UsernamePasswordAuthenticationFilter.class
                // Handles the Logout Mechanism of the Applications
                ).logout( logout -> logout
                        .logoutUrl("/auth/logout").permitAll()
                        .addLogoutHandler(logoutService) // Custom Logout Service for JWT Authentication
                        .logoutSuccessHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext(); // Clears the Security Context of the User.
                        })
                        // Deletes any Session Cookies that might have been created to invalidate the session totally
                        .deleteCookies("JSESSIONID"));
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
