package com.sist.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.*;
import com.sist.web.service.*;
import com.sist.web.vo.*;
import org.springframework.web.bind.annotation.RequestMapping;

// 화면만 변경해주는 역할
@Controller // router 기능
@RequiredArgsConstructor
public class RouterController {

	private final FoodService fService;
	
	@GetMapping("/")
	public String main_main(Model model)
	{
		model.addAttribute("main_html","main/home");
		return "main/main";
	}
	
	@GetMapping("food/detail_before")
	public String fiid_detail_before(@RequestParam("no") int no, HttpServletResponse response, RedirectAttributes ra)
	{
		// value값은 String으로 돼있음 => 무조건 문자열로만 저장이 가능하다
		Cookie cookie = new Cookie("food_"+no, String.valueOf(no));
		cookie.setPath("/"); // 저장 위치
		cookie.setMaxAge(60*60*24); // 저장 기간
		// 같은 메소드에서 두개를 동시에 처리하는 것은 불가능해서 reponse를 통해 쿠키를 보낼지 html을 보낼지 먼저 정해야한다
		response.addCookie(cookie); // 브라우저로 전송
		
		// 값 보내기 => no가 있어야 상세보기로 넘어가지
		ra.addAttribute("no",no); // 이걸 주면 redirect:/food/detail?no 이렇게 된대
		return "redirect:/food/detail"; // 이쪽으로 이동 후 html 보낼거야
	}
	
	@GetMapping("/food/detail")
	public String food_detail(@RequestParam("no") int no, Model model)
	{
		// html 보내주는 역할
		// model.addAttribute("no",no);
		
		FoodVO vo = fService.foodDetailData(no);
		model.addAttribute("vo",vo);
		model.addAttribute("main_html","food/detail");
		return "main/main";

	}
	@RequestMapping("/member/login")
	   public String member_login(Model model)
	   {
		   model.addAttribute("main_html", "member/login");
		   return "main/main";
	   }
	}

