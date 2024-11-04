package com.errday.springsecuritypractice1.security.config;

import com.errday.springsecuritypractice1.security.handler.FormAccessDeniedHandler;
import com.errday.springsecuritypractice1.security.handler.FormAuthenticationFailureHandler;
import com.errday.springsecuritypractice1.security.handler.FormAuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    private final FormAuthenticationSuccessHandler formAuthenticationSuccessHandler;
    private final FormAuthenticationFailureHandler formAuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*", "/*/icon-*").permitAll() // 정적 자원 설정
                .requestMatchers("/", "/signup", "/login*").permitAll()
                .requestMatchers("/user").hasAuthority("ROLE_USER")
                .requestMatchers("/manager").hasAuthority("ROLE_MANAGER")
                .requestMatchers("/admin").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .authenticationDetailsSource(authenticationDetailsSource) // 추가적인 정보를 인증 객체에 추가할 수 있다. 그 작업을 처리해주는 클래스 지정
                .successHandler(formAuthenticationSuccessHandler)   // 로그인 성공 handler 추가
                .failureHandler(formAuthenticationFailureHandler)   // 로그인 실패 handler 추가
                .permitAll()) // 커스텀 로그인 페이지 설정
            //.userDetailsService(userDetailsService) // 커스텀 UserDetailServce 설정 (custom authenticationProvider를 설정하면 필요없어진다)
            .authenticationProvider(authenticationProvider) // 커스텀 AuthenticationProvider 설정
            .exceptionHandling(exceptioin -> exceptioin
                .accessDeniedHandler(new FormAccessDeniedHandler("/denied")))   // 커스텀 AccessDeniedHandler
        ;

        return http.build();
    }



}
