package com.microservices.demo.elastic.query.service.config;

import com.microservices.demo.config.UserConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity // To enable spring to allow security logic in this class.
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserConfigData userConfigData;

    public WebSecurityConfig(UserConfigData userData) {
        this.userConfigData = userData;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/**").hasRole("USER")
                .and()
                // cross-site-request-forgery: an attack to use already-authenticated user's session to do unwanted actions, from the browser
                // The defense requires the browser to send a token to server.
                // Since our service doesn't take browser interactions, we can disable it and remove one spring security filter.
                .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(userConfigData.getUsername())
                .password(passwordEncoder().encode(userConfigData.getPassword()))
                .roles(userConfigData.getRoles());
    }

    // The encoder can ensure that we don't store the plain text of password in memory
    // But only the hashed value of the password in memory.
    // Comparison is done only on the hashed values.
    @Bean
    protected PasswordEncoder passwordEncoder() {
        // BCrypt encoder applies a strong hash function with iteration counts
        // in addition to a salt to defend against rainbow table attacks.
        return new BCryptPasswordEncoder();
    }


}