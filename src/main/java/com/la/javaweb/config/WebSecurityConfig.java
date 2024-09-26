package com.la.javaweb.config;

import com.la.javaweb.security.jwt.CustomAccessDeniedHandler;
import com.la.javaweb.security.jwt.JwtEntryPoint;
import com.la.javaweb.security.jwt.JwtTokenFilter;
import com.la.javaweb.security.user_principal.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	
	@Autowired
	private JwtEntryPoint jwtEntryPoint;
	@Autowired
	private JwtTokenFilter jwtTokenFilter;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;
	@Autowired
	private UserDetailService userDetailsService;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				  .cors(auth -> auth.configurationSource(request -> {
					  CorsConfiguration config = new CorsConfiguration();
					  config.setAllowedOrigins(List.of("*"));
					  config.setAllowedMethods(List.of("*"));
					  config.addAllowedHeader("*");
					  return config;
				  }))
				  .csrf(AbstractHttpConfigurer::disable)
				  .authenticationProvider(authenticationProvider())
				  .authorizeHttpRequests((auth) ->
							 auth.requestMatchers("/auth/**").permitAll()
									 .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
//									 .requestMatchers("/admin/**").permitAll()
									 .requestMatchers("/categories").permitAll()
									 .requestMatchers("/products/**").permitAll()
									 .anyRequest().authenticated())


				  .exceptionHandling((auth) ->
							 auth.authenticationEntryPoint(jwtEntryPoint)
										.accessDeniedHandler(customAccessDeniedHandler))
				  .sessionManagement((auth) -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				  .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
				  .logout(logout -> logout
						.logoutUrl("/auth/logout")
						.logoutSuccessHandler((request, response, authentication) -> {
							response.setStatus(HttpStatus.OK.value());
						})
				  )
				  .build();
	}
	
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}
}
