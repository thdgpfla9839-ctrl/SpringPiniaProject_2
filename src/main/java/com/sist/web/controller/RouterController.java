package com.sist.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// 화면만 변경해주는 역할
@Controller // router 기능
public class RouterController {

	@GetMapping("/")
	public String main_main(Model model)
	{
		model.addAttribute("main_html","main/home");
		return "main/main";
	}
	
	@GetMapping("/food/detail")
	public String food_detail(@RequestParam("no") int no, Model model)
	{
		model.addAttribute("no",no);
		model.addAttribute("main_html","food/detail");
		return "main/main";

	}
}
