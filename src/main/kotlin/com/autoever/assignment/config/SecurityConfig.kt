package com.autoever.assignment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { it.frameOptions { frame -> frame.sameOrigin() } } // H2 콘솔 허용
            .authorizeHttpRequests {
                it
                    .requestMatchers("/h2-console/**").permitAll() // H2 콘솔
                    .requestMatchers("/api/users").permitAll()     // 회원가입
                    .anyRequest().permitAll()                      // 그 외 허용 (임시)
            }
            .httpBasic(withDefaults()) // admin용 Basic Auth 설정 대비용

        return http.build()
    }
}
