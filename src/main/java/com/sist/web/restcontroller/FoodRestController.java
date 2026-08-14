package com.sist.web.restcontroller;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.web.service.*;
import com.sist.web.vo.*;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor
public class FoodRestController {

	private final FoodService fService;
	
	// 전체 목록 출력
	@GetMapping("/food/list_vue")
	public ResponseEntity<Map> food_list(@RequestParam("page") int page)
	{
		Map map = new HashMap();
		try
		{
			// 목록 가져오기
			List<FoodVO> list = fService.foodListData(page);
			int[] pages=fService.foodPages(page);
			
			map.put("list", list);
		   // 풀어서 보내줄지 자바스크립트에서 어쩌구? 이건 더 찾아보기
			map.put("curpage", pages[0]);
			map.put("totalpage", pages[1]);
			map.put("startPage", pages[2]);
			map.put("endPage", pages[3]);
			
		}
		catch (Exception ex) 
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();	

			
		}
		return ResponseEntity.ok(map); // ok() => 200
	}
}
