package com.raehyeon.vroom.security.config;

import com.raehyeon.vroom.role.domain.RoleType;
import com.raehyeon.vroom.security.jwt.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션을 사용하지 않고, JWT 같은 토큰 기반 인증을 쓰겠다는 설정 (STATELESS).
            .httpBasic(AbstractHttpConfigurer::disable) // 기본 제공되는 HTTP Basic 인증을 비활성화.
            .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource())) // 아래에서 정의한 CORS 설정을 적용.
            .csrf(AbstractHttpConfigurer::disable) // CSRF 보호를 비활성화. 주로 API 서버에서는 CSRF가 필요 없어서 꺼버림.
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(request -> request
                .requestMatchers("/ws/**").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name()) // ws 경로에 대한 요청 허용
                .requestMatchers("/", "/api/auth/login").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/members/me").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/members/nickname").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/members/me/chat-rooms").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/members/search").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())

                .requestMatchers(HttpMethod.POST, "/api/chat-rooms").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/chat-rooms").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/chat-rooms/search").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/chat-rooms/{chatRoomId}").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/chat-rooms/{chatRoomId}/passwordRequired").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.PATCH, "/api/chat-rooms/{chatRoomId}/name").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.POST, "/api/chat-rooms/{chatRoomId}/participants").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.DELETE, "/api/chat-rooms/{chatRoomId}/participants").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())
                .requestMatchers(HttpMethod.GET, "/api/chat-rooms/{chatRoomId}/participants").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())


                .requestMatchers(HttpMethod.GET, "/api/chat-rooms/{chatRoomId}/messages").hasAnyRole(RoleType.MEMBER.name(), RoleType.ADMIN.name())



                .anyRequest().authenticated()
            );

        return httpSecurity.build(); // 설정을 마치고 보안 필터 체인 객체를 리턴.
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // 허용할 HTTP 메서드들 지정
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // 요청 시 허용할 헤더들 지정.
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "X-Requested-With", "Accept", "Origin"));

        // 쿠키나 인증 정보를 포함한 요청을 허용할지 여부
        configuration.setAllowCredentials(true);

        // 응답 헤더 중 클라이언트에서 접근 가능한 헤더를 지정
        configuration.setExposedHeaders(List.of("Authorization"));

        // 브라우저가 CORS 정책을 캐싱하는 시간(초) 설정 — 1시간
        configuration.setMaxAge(3600L);

        // CORS 정책을 모든 경로(/**)에 적용해서 반환.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
