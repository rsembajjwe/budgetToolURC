package com.methaltech.application.security;

import com.methaltech.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    private final CustomAuthenticationFailureHandler failureHandler;

    public SecurityConfiguration(CustomAuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/icons/*.png")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/VAADIN/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/vaadinServlet/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/robots.txt")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/manifest.webmanifest")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/sw.js")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/offline.html")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/frontend/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/lumo-css-framework/**")).permitAll()
                .requestMatchers("/VAADIN/dynamic/**").permitAll()
                .requestMatchers("/VAADIN/dynamic/resource/**").permitAll()
        );

        http.formLogin(form -> form
                .failureHandler(failureHandler)
        );

        super.configure(http);
        setLoginView(http, LoginView.class);
    }
}
