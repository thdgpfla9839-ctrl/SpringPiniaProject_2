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
	         // 저장기간을 1일로 설정
	         .tokenValiditySeconds(60*60*24)
	         // persistent_logins 테이블에 저장
	         .tokenRepository(persistentTokenRepository())
	    )
	    // 로그아웃 실행
	    .logout(logout -> logout 
	          .logoutUrl("/member/logout")
	          .logoutSuccessUrl("/")
	          .invalidateHttpSession(true)
	          // 쿠키를 삭제 
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
   // 이 부분이 있어야 자동로그인이 돼어 저장이 된다
   @Bean
   public PersistentTokenRepository persistentTokenRepository() {
       JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
       repo.setDataSource(dataSource);
       return repo;
   }
   
}

/*
 *   최종 정리
 *   [사용자] => post방식으로 액션을 날림 /meber/login_process에서 시작
 *     |
 *  SpringSecurity FilterChain을 거침
 *   : 인증과정을 거침
 *     |
 *  UsernamePasswordAuthenticationFilter
 *    : .usernameParameter / .passwordParameter
 *     |
 *  AuthenticationManager
 *    : DB 검색
 *     |
 *  AuthenticationProvider
 *     |
 *  JdbcUserDetailManager  
 *    : 1) springmember => 기본 사용자 정보 2)authority => 권한 정보
 *         두 테이블에서 해당 정보를 가져온다
 *         다만 username이 존재하는 상태에서 가능하다
 *     |
 *  UserDetails로 저 두 정보를 모아준다
 *     |
 *  BCryptPasswordEncoder
 *   : 비밀번호 정보를 검증
 *     |
 *   비밀번호 
 *  성공 / 실패  
 *  두가지로 나뉜다
 *  1) 성공 : LoginSuccessHandler - SecurityContext - Session에 저장 - 인증완료
 *  2) 실패 : LoginFailHandler
 *     
 *          
 */

/*
 *  [라이브러리 역할]
 *  @EnableWebSecurity : Spring Security 활성화를 시키는 이유는 사용하기 위함이다
 *  
 *  [알아둬야 하는 클래스]
 *  1. SecurityConfig : 보안 전체를 설계(설정)를 담당하는 부분 => 사용자 정의(개발자가 만들어야함)
 *  2. HttpSecurity   : 주로 로그인이나 로그아웃, 권한, CSRF 보안 설정을 구성
 *  3. SecurityFilterChain : HTTP 요청에 대한 Spring Security 필터 처리 순서 정의
 *  4. AuthenticationManager : 사용자의 인증과정을 총괄
 *  5. AuthenticationProvider : 실제 사용자 인증을 수행하는 객체 => login_ok
 *  6. UserDetailsService    : 로그인한 사용자의 정보를 조회
 *  7. JdbcUserDetailsManager : DB에서 사용자나 권한 정보를 조회해서 저장해주는 역할 => 반드시 SQL문장을 사용해야한다
 *  8. UserDetails      :  사용자 정보가 저장된 객체
 *  9. BCryptPasswordEncoder : 비밀번호 암호화를 처리하는 곳
 *  10. JdbcTokenRepositoryImpl : 자동로그인 시 사용자 구분, 토큰을 저장하는 역할
 *  11. LoginSuccessHandler : 로그인 성공 시 처리 => 개발자가 만든 부분
 *  12. LoginFailHandler : 로그인 실패 시 처리되는 부분 => 역시 개발자가 만듦
 *  13. SecurityContext : 인증 정보를 보관하는 클랫,
 *  14. formLogin : 로그인 시 처리방법을 설정
 *  15. rememberMe : 자동로그인 설정하는 부분
 *  16. logout : 말그대로 로그아웃 => 세션해체, 쿠키삭제를 담당
 *  
 *  => Filter - Manager - UserDetailsService - DB - PasswordEncoder - Authentication - SecurityContext
 *  => DB에는 회원, 권한 정보를 가지고 있음
 *  => Authentication에는 로그인된 사용자 정보만 가지고 있음
 *  => SecurityContext에는 Authentication에 저장돼 있는 정보를 보관
 *  => Session은 로그인 상태 유지 => 세선에 저장하면 리멤버미가 가능 세션을 해체하기 전까지
 *  
 *  => 인증 - 성공 - 유지 부분만 잘 기억하면 됨
 *  => 인증 : AuthenticationManager
 *  => 성공 : Authentication - SecurityContext
 *  => 유지 : Session (remember-me) => cookie + DB
 */
