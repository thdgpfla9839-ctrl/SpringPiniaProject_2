package com.sist.web.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.sist.web.security.LoginFailHandler;
import com.sist.web.security.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

@Configuration // xml을 자바로 변경 => 설정을 쉽게 만들기 위해 / 보안 문제 때문에 변경함
@EnableWebSecurity // Security => 인터셉터 문제 때문에 먼저 설정
@RequiredArgsConstructor // 롬복에 해당 => 생성자를 통해 @Autowired
// 객체를 자동주입하려면 final을 준다
/*
 *   1. Spring Security 
 *      = 보안을 담당하는 프레임워크 
 *        ---
 *        | 인증 / 인가 
 *          ---   ---
 *                Authorization : 사용자가 누구인지 확인 절차 = 로그인 
 *          Authentication :  인증된 사용자가 사이트에 접근 가능한지 권한 확인 
 *        | 저장 (인증 = 권한 = 저장 = Session)
 *                          -----
 *                          서버가 종료 => 메모리 해제 
 *                          | => Cookie 기반 : JWT 
 *          1) 인증 => 회원에 가입된 자 / 게스트 / 관리자 
 *             |= DispatcherServlet => HandlerMapping
 *              = 인터셉트 
 *              |
 *              사용자 요청 ====== DispatcherServlet => HandlerMapping
 *                        | preHandle()                  | => postHandle()
 *                                                    ViewResolver
 *                                                         | => 
 *                                                       JSP
 *          2) Authentication Filter 
 *               | 책임 전가 
 *          3) Authentication Manager
 *               | 인증 방법 
 *          4) Authentication Provider
 *             DataBase연동 => 5) UserDetailService : 결과값 return
 *                |
 *                4-1) User비교 / 암호 
 *                     | PasswordEncoding 
 *         
 *         /login => permitAll
 *         /admin => hasRole("ROLE_ADMIN")
 *         /user  => hasRole("ROLE_USER")
 *         /board => permitAll
 *         /member => permitAll
 *         ====================================================================
 */

    

public class SecurityConfig {
   private final LoginSuccessHandler loginSuccessHandler;
   private final LoginFailHandler  loginFailHandler;
   private final DataSource dataSource;
   
   // 접근 권한 => SecurityFilterChain
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http)
   throws Exception
   {
	   // 공격 방어 
	   /*
	    *     CSRF 
	    *       Cross site Request forgery 
	    *       공격자가 인증된 브라우저에서 
	    *       저장된 쿠키나 세션정보를 활용해서 
	    *       => 다른 사이트 요청값 전달 : 위조 
	    *       => 일반 보안 =: csrf.disable()
	    */
	   http
	    .csrf(csrf-> csrf.disable())
	    // 접근 권한 설정 => url 대상으로 설정
	    .authorizeHttpRequests(auth-> auth
	    		// /member/** => 멤버 로그인 / 멤버 로그아웃을 의미
	    		// permit은 로그인 없이 접근이 가능하다
	          .requestMatchers("/","/member/**").permitAll()
	          // admin권한이 있는 사람만 접근이 가능
	          .requestMatchers("/admin/**").hasRole("ADMIN")
	          // anyRequest=> 지정되지 않은 url 주소 permitAll => 누구나 접근 가능
	          .anyRequest().permitAll()
	          // 추가로 /comment/** => authenticated()이렇게 줄 수 있음
	    )
	    // 로그인 설정하는 부분 => 자체 내에서 설정
	    .formLogin(form -> form 
	    		// loginPage 로그인 화면창 설정 => 설정이 없는 경우는 default login을 한다
	          .loginPage("/member/login")
	          // 가장 중요한 부분 => 로그인 자체 처리를 담당하는 URL =>  login_process 이 부분은 security에서 인터셉트가 되게끔 가상으로 만든 url
	          // Controller가 처리하는 게 아니라 SecurityFilter에서 처리된다
	          .loginProcessingUrl("/member/login_process")
	          .usernameParameter("userid")
	          // 로그인 요청을 위해 id와 pwd를 전송
	          // id를 인식 못하고 username으로 인식
	          // pwd => password로 인식
	          .passwordParameter("userpwd")
	          .defaultSuccessUrl("/",false)
	          .successHandler(loginSuccessHandler)
	          .failureHandler(loginFailHandler)
	          .permitAll() 
	    )
	    // 자동로그인
	    .rememberMe(remember-> remember
	         .key("my-secret-key")
	         .rememberMeParameter("remember-me")
	         .tokenValiditySeconds(60*60*24)
	         .tokenRepository(persistentTokenRepository())
	    )
	    
	    .logout(logout -> logout 
	          .logoutUrl("/member/logout")
	          .logoutSuccessUrl("/")
	          .invalidateHttpSession(true)
	          .deleteCookies("remember-me","JSESSIONID")
	    );
	    // remember-me
	    return http.build();
	    
   }
   
// 인증 관리자 
   @Bean
   public AuthenticationManager authenticationManager(
      HttpSecurity http,
      BCryptPasswordEncoder passwordEncoder
   ) throws Exception
   {
	   AuthenticationManagerBuilder builder=
			   http.getSharedObject(AuthenticationManagerBuilder.class);
	   builder
	   // userDetailsService => 사용자 정보를 저장(세션형식으로 구성된 principal에 저장) jdbcUserDetailsService => 비교를 위해 데이터베이스 검색과 SQl문장이 들어간다
	     .userDetailsService(jdbcUserDetailsService())
	     // passwordEncoder => 비밀번호 비교가 이뤄짐
	     .passwordEncoder(passwordEncoder());
	   return builder.build();
   }
   @Bean
   public JdbcUserDetailsManager jdbcUserDetailsService() {
	   JdbcUserDetailsManager manager=
			   new JdbcUserDetailsManager(dataSource);
	   manager.setUsersByUsernameQuery(
			   "SELECT userid as username,userpwd as password,enable "
			   +"FROM springmember WHERE userid=?"
	   );
	   manager.setAuthoritiesByUsernameQuery(
			   "SELECT userid as username , authority "
			  +"FROM authority WHERE userid=?"
	   );
	   return manager; // manager 속 내용은 principal에 저장된다
   }
// 비밀번호 암호화 
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
	   return new BCryptPasswordEncoder();
   }
   
// PersistentLogins 등록 => 자동로그인
   @Bean
   public PersistentTokenRepository persistentTokenRepository() {
       JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
       repo.setDataSource(dataSource);
       return repo;
   }
   
}
