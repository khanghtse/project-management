package khanghtse.com.projectmanagement.configurations;

import khanghtse.com.projectmanagement.security.JwtAuthenticationFilter;
import khanghtse.com.projectmanagement.security.oauth2.CustomOAuth2UserService;
import khanghtse.com.projectmanagement.security.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    // Inject thêm 2 bean xử lý OAuth2
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // Cấu hình CORS để Frontend (React) gọi được API
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
                    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    //configuration.setAllowedHeaders(List.of("*"));
                    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
                    configuration.setExposedHeaders(List.of("Authorization")); // Cho phép client đọc header này
                    configuration.setAllowCredentials(true);
                    return configuration;
                }))
                .authorizeHttpRequests(auth -> auth
                        // Cho phép các endpoint Auth và OAuth2 truy cập không cần token
                        .requestMatchers("/api/v1/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. FIX LỖI REDIRECT & CORS TẠI ĐÂY
                // Chỉ định rõ: Với các URL bắt đầu bằng /api/**, nếu chưa login -> Trả về 401 luôn.
                // Không để Spring tự động redirect sang trang login của Google.
                .exceptionHandling(e -> e
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                PathPatternRequestMatcher.pathPattern("/api/**")
                        )
                )

                // --- CẤU HÌNH OAUTH2 (GOOGLE) ---
                .oauth2Login(oauth2 -> oauth2
                        // Base URI để Frontend gọi: http://localhost:8080/oauth2/authorization/google
                        .authorizationEndpoint(e -> e.baseUri("/oauth2/authorization"))
                        // URI Redirect Google trả về
                        .redirectionEndpoint(e -> e.baseUri("/login/oauth2/code/*"))
                        // Service xử lý lấy thông tin user từ Google
                        .userInfoEndpoint(e -> e.userService(customOAuth2UserService))
                        // Handler xử lý sau khi login thành công (Tạo JWT, set Cookie)
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                );

        // Thêm Filter kiểm tra JWT trước filter mặc định
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
