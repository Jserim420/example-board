package com.example.exampleboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import groovyjarjarantlr4.v4.runtime.atn.SemanticContext.AND;
import jakarta.servlet.DispatcherType;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
	
	 
	 @Bean
	    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
	        return http.csrf().disable()
	        		.authorizeHttpRequests()
	        		// .requestMatchers("/api/board/**", "/board/**").authenticated()
	        		.anyRequest().permitAll()
	        		.and().build();
	    }
	 
}
