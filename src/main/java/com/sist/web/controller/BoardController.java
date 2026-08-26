package com.sist.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sist.web.entity.BootBoard;
import com.sist.web.service.BoardServiceImpl;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardServiceImpl bDao;
	
	// 여기는 일반 타임리프
	@GetMapping("/board/list")
	// required = false : 널값 허용
	public String board_list(@RequestParam(value =  "page", required = false) String page, Model model)
	{
		if(page==null)
			page="1";
		int curpage = Integer.parseInt(page);
		int rowSize=10;
		// 페이지 나누기
		Pageable pg =PageRequest.of(curpage-1, rowSize, Sort.by(Sort.Direction.DESC,"no"));
		Page<BootBoard> pList = bDao.findAll(pg);
		List<BootBoard> list = new ArrayList<BootBoard>();
		// pList를 List 형식으로 변경
		if(pList!=null && pList.hasContent())
		{
			list = pList.getContent();
		}
		
		// 총페이지 가져오기
		int totalpage = bDao.boardTotalPage();
		// 데이터 보내기
		model.addAttribute("list",list);
		model.addAttribute("curpage",curpage);
		model.addAttribute("totalpage",totalpage);
		model.addAttribute("main_html","board/list");
		return "main/main";
	}
	
	@GetMapping("/board/insert")
	public String board_insert(Model model)
	{
		model.addAttribute("main_html","board/insert");
		return "main/main";
	}
	
	@PostMapping("/board/insert_ok")
	// 보낼값은 없고 가져오기만 하면 됨
	public String board_insert_ok(@ModelAttribute("vo") BootBoard vo)
	{
		bDao.save(vo);
		return "redirect:/board/list";
	}
	
	@GetMapping("/board/detail")
	public String board_detail(@RequestParam("no") int no, Model model)
	{
		BootBoard vo = bDao.findByNo(no);
		// 조회수 증가
		vo.setHit(vo.getHit()+1);
		bDao.save(vo);
		vo = bDao.findByNo(no);
		model.addAttribute("vo",vo);
		model.addAttribute("main_html","board/detail");
		return "main/main";
	}
}
