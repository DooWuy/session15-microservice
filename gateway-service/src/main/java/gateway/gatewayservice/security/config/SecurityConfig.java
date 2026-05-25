package gateway.gatewayservice.security.config;

import gateway.gatewayservice.security.filter.GatewayHeaderFilter;
import org.springframework.context.annotation.Bean;

public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Tất cả request đều phải qua bộ lọc và cần xác thực dựa trên Method Security (@PreAuthorize)
                        .anyRequest().authenticated()
                )
                // Thêm filter đọc Header vào TRƯỚC filter xác thực mặc định của Spring
                .addFilterBefore(new GatewayHeaderFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
