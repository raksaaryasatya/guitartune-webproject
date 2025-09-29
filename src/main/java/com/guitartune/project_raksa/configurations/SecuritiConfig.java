package com.guitartune.project_raksa.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.guitartune.project_raksa.constant.RoleConstant;

@Configuration
@EnableWebSecurity
public class SecuritiConfig {

    // Bean untuk enkripsi password menggunakan BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Logger untuk mencatat informasi selama proses security
    private static final Logger logger = LoggerFactory.getLogger(SecuritiConfig.class);

    // Konfigurasi filter keamanan
    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Mengaktifkan CORS (Cross-Origin Resource Sharing) dan menonaktifkan CSRF (Cross-Site Request Forgery)
                .cors().and().csrf(csrf -> csrf.disable())
                
                // Mengatur akses endpoint berdasarkan otorisasi
                .authorizeHttpRequests(auth -> auth
                        // Endpoint yang dapat diakses tanpa login
                        .requestMatchers("/login", "/register", "/user/save-user", "/home").permitAll()
                        
                        // Endpoint yang hanya bisa diakses oleh pengguna dengan role USER
                        .requestMatchers("/user/**", "/store/**", "/product/**", "/buy/**")
                        .hasAuthority(RoleConstant.ROLE_USER)
                        
                        // Endpoint yang hanya bisa diakses oleh pengguna dengan role ADMIN
                        .requestMatchers("/admin/**").hasAuthority(RoleConstant.ROLE_ADMIN)
                        
                        // Endpoint lainnya memerlukan login
                        .anyRequest().authenticated()
                )
                
                // Konfigurasi halaman login
                .formLogin(login -> login
                        .loginPage("/login") // URL untuk halaman login
                        .successHandler((request, response, authentication) -> {
                            // Mengambil role user setelah berhasil login
                            String role = authentication.getAuthorities().iterator().next().getAuthority();
                            logger.info("User role after login: " + role); // Log untuk mengecek role

                            // Menentukan URL redirect berdasarkan role
                            String redirectUrl = role.contains(RoleConstant.ROLE_ADMIN) ? "/admin/dashboard" : "/home";
                            logger.info("Redirecting to: " + redirectUrl); // Log untuk memastikan redirect URL
                            response.sendRedirect(redirectUrl); // Melakukan redirect
                        })
                        .failureUrl("/login?error=true") // Redirect ke login jika login gagal
                        .permitAll()
                )
                
                // Konfigurasi logout
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL untuk logout
                        .logoutSuccessUrl("/login") // Redirect ke halaman login setelah logout
                        .permitAll()
                );

        return http.build(); // Membangun konfigurasi security
    }

    // Bean untuk menangani proses logout
    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            if (authentication != null) {
                // Menghapus informasi otentikasi dari SecurityContext
                SecurityContextHolder.clearContext();
                // Menghapus session pengguna
                request.getSession().invalidate();
            }
        };
    }
}
