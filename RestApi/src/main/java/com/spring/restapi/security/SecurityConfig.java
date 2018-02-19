package com.spring.restapi.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Security configuration for our REST API server. Requires HTTP user auth for
 * the GET, PUT, and DELETE methods. Permits all connections for the POST
 * method.
 * 
 * @author Chris Leung
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.csrf().disable()
        	.authorizeRequests()
        		.antMatchers(HttpMethod.GET, "/**").authenticated()
        		.antMatchers(HttpMethod.PUT, "/**").authenticated()
        		.antMatchers(HttpMethod.DELETE, "**").authenticated()
        		.anyRequest().permitAll()
        		.and()
    		.httpBasic().and()
    		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}