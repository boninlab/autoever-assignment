package com.autoever.assignment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun userDetailsService(): UserDetailsService {
        val admin = User.withUsername("admin")
            .password("{noop}1212") // {noop}은 패스워드 인코딩 생략
            .roles("ADMIN")
            .build()

        return InMemoryUserDetailsManager(admin)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .headers { it.frameOptions { frame -> frame.sameOrigin() } } // H2 콘솔 허용
            .authorizeHttpRequests {
                it
                    .requestMatchers("/h2-console/**", "/api/users").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll()
            }
            .httpBasic { } // Basic 인증 사용

        return http.build()
    }
}
