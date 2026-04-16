package chekizybra.otzovichok.config;

import chekizybra.otzovichok.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserService userDetailsService;

    public SecurityConfig(UserService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html", "/login.html", "/register.html")
                        .permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/style/**")
                        .permitAll()
                        .requestMatchers("/auth/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/reviews/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/comments/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/categories/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/profile")
                        .authenticated()
                        .requestMatchers("/profile.html")
                        .authenticated()
                        .requestMatchers("/newcomment.html", "/newcomment.css", "/newcomment.js")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/comments")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/comments/*/upvote")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/comments/*/downvote")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/votes")
                        .authenticated()
                        .requestMatchers(HttpMethod.POST, "/votes/*/value")
                        .authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exep -> exep
                        .authenticationEntryPoint((request, response, authException) -> {
                            String ajaxHeader = request.getHeader("X-Requested-With");
                            String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

                            boolean isAjax = "XMLHttpRequest".equals(ajaxHeader);
                            boolean wantsJson = acceptHeader != null && acceptHeader.contains("application/json");

                            if (isAjax || wantsJson) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"message\":\"unauthorized\"}");
                            } else {
                                response.sendRedirect("/login.html");
                            }
                        })
                )
                .formLogin(form -> form
                        .loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"ok\"}");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"bad credentials\"}");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"logout ok\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:63342"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        c.setAllowedHeaders(List.of("*"));
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider a = new DaoAuthenticationProvider();
        a.setUserDetailsService(userDetailsService);
        a.setPasswordEncoder(passwordEncoder());
        return a;
    }
}