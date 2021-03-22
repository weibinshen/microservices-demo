package com.microservices.demo.config.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // There are encrypt and decrypt endpoint already compatible with Spring boot CLI.
    // Here the ignoring will bypass these two end points from authentication of spring security
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/actuator/**")
                .antMatchers("/encrypt/**")
                .antMatchers("/decrypt/**");
        super.configure(web);
    }
}