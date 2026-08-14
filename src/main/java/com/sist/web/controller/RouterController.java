package com.sist.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 화면만 변경해주는 역할
@Controller // router 기능
public class RouterController {

	@GetMapping("/")
	public String main_main(Model model)
	{
		model.addAttribute("main_html","main/home");
		return "main/main";
	}
}
