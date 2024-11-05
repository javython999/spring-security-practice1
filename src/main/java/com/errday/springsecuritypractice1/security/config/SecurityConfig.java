package com.errday.springsecuritypractice1.security.config;

import com.errday.springsecuritypractice1.security.filter.RestAuthenticationFilter;
import com.errday.springsecuritypractice1.security.handler.FormAccessDeniedHandler;
import com.errday.springsecuritypractice1.security.handler.FormAuthenticationFailureHandler;
import com.errday.springsecuritypractice1.security.handler.FormAuthenticationSuccessHandler;
import com.errday.springsecuritypractice1.security.handler.RestAuthenticationFailureHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationProvider restAuthenticationProvider;
    private final AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;
    private final FormAuthenticationSuccessHandler formAuthenticationSuccessHandler;
    private final RestAuthentictationSuccessHandler restAuthenticationSuccessHandler;
    private final FormAuthenticationFailureHandler formAuthenticationFailureHandler;
    private final RestAuthenticationFailureHandler restAuthenticationFailureHandler;

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

    @Bean
    @Order(1)
    public SecurityFilterChain restSecurityFilterChain(HttpSecurity http) throws Exception {

        // authenticationManager 가져오기
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(restAuthenticationProvider);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http
            .securityMatcher("/api/**") // /api를 포함하는 모든 요청을 처리한다.
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/css/**", "/images/**", "/js/**", "/favicon.*").permitAll()
                .anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable) // Rest 방식의 비동기 통신은 클라이언트에서 CSRF 값을 직접 전달해주어야 한다. 일단 비활성화 한다.
            .addFilterBefore(restAuthenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class) // UsernamePasswordAuthenticationFilter 앞에 restAuthenticationFilter를 추가해준다.
            .authenticationManager(authenticationManager)
        ;

        return http.build();
    }

    private RestAuthenticationFilter restAuthenticationFilter(AuthenticationManager authenticationManager) {
        RestAuthenticationFilter restAuthenticationFilter = new RestAuthenticationFilter();
        restAuthenticationFilter.setAuthenticationManager(authenticationManager);
        restAuthenticationFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        restAuthenticationFilter.setAuthenticationFailureHandler(restAuthenticationFailureHandler);
        return restAuthenticationFilter;
    }
}
