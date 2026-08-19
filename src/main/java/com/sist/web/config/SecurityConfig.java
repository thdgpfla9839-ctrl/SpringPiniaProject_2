package com.sist.web.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.sist.web.security.LoginFailHandler;
import com.sist.web.security.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration // 메모리할당을 위한 환경설정
@EnableWebSecurity
@RequiredArgsConstructor 
public class SecurityConfig {

	private final LoginSuccessHandler loginSuccessHandler;
	private final LoginFailHandler loginFailHandler;
	private final DataSource dataSource;
	
	// 접근 권한 => SecurityFilterChain
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf(csrf-> csrf.disable()).authorizeHttpRequests(auth-> auth.requestMatchers("/","/member/**")
				         .permitAll().requestMatchers("/admin/**").hasRole("ADMIN")
				         .anyRequest().permitAll()).formLogin(form-> form.loginPage("/member/login")
				         .loginProcessingUrl("/member/login_process")
				         .usernameParameter("userid")
				         .passwordParameter("userpwd")
				         .defaultSuccessUrl("/",false)
				         .successHandler(loginSuccessHandler)
				         .failureHandler(loginFailHandler)
				         .permitAll()).rememberMe(remember-> remember.key("my-secret-key")
							        	   	 .rememberMeParameter("remember-me")
							        		 .tokenValiditySeconds(60*60*24))
				                    .logout(logout-> logout.logoutUrl("/member/logout")
				        		    .logoutSuccessUrl("/")
				        		    .invalidateHttpSession(true));
		
		 return http.build();
	}
	// 권한 처리 => 권한을 설정할 때 1) permitAll : 모든 사람이 권한 O
	//                        2) hasRole : 내가 지정한 사람만 권한 ('ROLE_ADMIN')
	// 로그인 처리
	// 로그아웃 처리 
	// 리멤버-미 처리
	
	// 인증관리자를 먼저 거쳐야 한다
	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder) throws Exception
	{
		return null;
	}
	
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsService()
	{
		return null;
	}
	// 비밀번호 암호화
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	// PersistentLogins 등록
	@Bean
	public PersistentTokenRepository persistentTokenRepository()
	{
		return null;
	}
	
}
