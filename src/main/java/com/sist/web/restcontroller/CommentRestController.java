package com.sist.web.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sist.web.vo.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import com.sist.web.service.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

	private final CommentService cService;
	
	public Map commonsData(int page, int fno)
	{
		Map map = new HashMap();
		int start = (page*10)-10;
		List<CommentVO> list = cService.commentListData(start,fno);
		int count = cService.commentRowCount(fno);
		int totalpage = (int)(Math.ceil(count/10.0));
		
		// map에 값 넣기
		map.put("rList", list);
		map.put("count", count);
		map.put("curpage", page);
		map.put("totalpage", totalpage);
		return map;
	}
	
	// 값 보내기
	@GetMapping("/comment/list_vue")
	public ResponseEntity<Map> comment_list(@RequestParam("page") int page, @RequestParam("fno") int fno)
	{
	  Map map = new HashMap<>();
	  
	  try 
	  {
		 map = commonsData(page, fno);
	  }
	  catch (Exception ex) 
	  {
		ex.printStackTrace();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	  }
	  return ResponseEntity.ok(map);
	}
	
	@PostMapping("/comment/insert_vue")
	// vue는 값을 넘길 때 {} 넘기는데 이걸 vo가 받아야 한다
	// 자바스크립트에서 json으로 넘어오니까 그냥 vo로 받으면 안 되고 객체단위로 변경해서 받아와야한다 @RequestBody
	// html에서 넘어오는 일반 데이터를 받을 때는 @modelAttribute
	public ResponseEntity<Map> comment_insert(@RequestBody CommentVO vo, HttpSession session)
	{

		  Map map = new HashMap<>();
		  
		  try 
		  {
			 String id =(String)session.getAttribute("userid"); // LoginSuccessHandler에 그렇게 설정해둠
			 String name =(String)session.getAttribute("username");
			 vo.setId(id);
			 vo.setName(name); // 이거 두개는 세션에 저장돼 있어서 값을 안 보낼거야
			 cService.commentInsert(vo); // 갱신되면 변경된 데이터 디비에 보내줘야해
			 map = commonsData(vo.getPage(), vo.getFno()); 
		  }
		  catch (Exception ex) 
		  {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		  }
		  return ResponseEntity.ok(map);
	}
}
