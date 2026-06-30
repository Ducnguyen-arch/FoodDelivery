package com.foodecommerce.foodies.config;

import com.foodecommerce.foodies.filter.JwtAuthenticationFilter;
import com.foodecommerce.foodies.service.FactUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

//Flow: Request -> CorsFilter (check origin) => SecurityFilterChain ---> permitAll or y/c Auth ---< Controller
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FactUserDetailsService factUserDetailsService;
    private final JwtAuthenticationFilter  jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        "/api/register", "/api/login", "/api/foods/**", "/api/orders/all-orders","/api/orders/**",
                                "/api/orders/status/**").permitAll()
                        // Authentication được thực hiện qua apikey header.
//                        .requestMatchers(
//                                HttpMethod.GET,
//                                "/api/payments/webhook/**"
//                        ).permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/payments/webhook/**"
                        ).permitAll()
                        .anyRequest().authenticated()) //tat ca endpoint con lai phai co token valid
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))// Không tạp HTTP Session. Mỗi request phải có token(JWT) - Stateless API
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    //Dùng Bcrypt để Hash password trước khi save password vào DB
    //Spring Security tự inject bean này vào AuthenticationManager để verify.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(urlBasedCorsConfigurationSource());
    }

    private UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //chỉ cho phép acccess từ 2 origins này (phía front-end)
        //"https://steerable-turmoil-donated.ngrok-free.dev/**"
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        //OPTIONS bắt buộc phải có — browser gửi preflight request bằng OPTIONS trước khi gửi request thật.
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        //Gửi JWT, header phổ biến browser, json body
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        //cho phép gửi cookie/credential kèm request. AllowedOrigins không được dùng wildcard '*'
        corsConfiguration.setAllowCredentials(true);

        // Config riêng cho webhook — cho phép tất cả origin (SePay server-to-server)
        CorsConfiguration webhookConfig = new CorsConfiguration();
        webhookConfig.addAllowedOriginPattern("*");
        webhookConfig.setAllowedMethods(List.of("POST"));
        webhookConfig.setAllowedHeaders(List.of("*"));
        webhookConfig.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //áp dụng config cho all paths
        source.registerCorsConfiguration("/api/payments/webhook/**", webhookConfig); // phải đăng ký trước
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(factUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authenticationProvider);
    }
}
