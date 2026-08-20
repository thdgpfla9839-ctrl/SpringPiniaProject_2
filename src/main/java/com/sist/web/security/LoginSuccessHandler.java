package com.sist.web.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import com.sist.web.mapper.*;
import com.sist.web.vo.*;
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler{

	private final MemberMapper mapper;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// 호출하려는 유저아이디는 authentication 이 안에 있음
		MemberVO vo = mapper.memberInfoData(authentication.getName()); // getName()이 id임
		HttpSession session = request.getSession();
		session.setAttribute("userid", vo.getUserid());
		session.setAttribute("username", vo.getUsername());
		session.setAttribute("sex", vo.getSex());
		response.sendRedirect("/");
	}


}