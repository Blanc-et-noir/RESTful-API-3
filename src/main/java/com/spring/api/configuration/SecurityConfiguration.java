package com.spring.api.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.spring.api.filter.JwtAuthenticationFilter;
import com.spring.api.handler.JwtAuthenticationDeniedHandler;
import com.spring.api.handler.JwtAuthenticationEntryPoint;
import com.spring.api.jwt.JwtTokenProvider;
import com.spring.api.util.RedisUtil;

@Configuration
public class SecurityConfiguration {
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAuthenticationDeniedHandler jwtAuthenticationDeniedHandler;
	private final RedisUtil redisUtil;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	SecurityConfiguration(
			RedisUtil redisUtil, 
			JwtTokenProvider jwtTokenProvider, 
			JwtAuthenticationFilter jwtAuthenticationFilter, 
			JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, 
			JwtAuthenticationDeniedHandler jwtAuthenticationDeniedHandler){
		
		this.redisUtil = redisUtil;
		this.jwtTokenProvider = jwtTokenProvider;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAuthenticationDeniedHandler = jwtAuthenticationDeniedHandler;	
	}
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .httpBasic().disable()
        .csrf().disable()
        .cors().configurationSource(configurationSource()).and()
        .authorizeRequests()
        .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
        
        .antMatchers(HttpMethod.OPTIONS).permitAll()
        .antMatchers(HttpMethod.POST,"/api/v1/tokens").permitAll()
        .antMatchers(HttpMethod.PUT,"/api/v1/tokens").permitAll()
        .antMatchers(HttpMethod.GET,"/api/v1/tokens").permitAll()
        .antMatchers(HttpMethod.POST,"/api/v1/users").permitAll()
        .anyRequest().authenticated().and()
        
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
        .accessDeniedHandler(jwtAuthenticationDeniedHandler);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("HEAD","GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}