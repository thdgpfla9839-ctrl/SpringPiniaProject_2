package com.sist.web.restcontroller;
// vue와 연결 => 여기서 대댓글이 올라가면 아이디가 같은게 들어오면 알림 날려줌
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.web.vo.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import com.sist.web.kafka.NoticeProducer;
import com.sist.web.mapper.*;
@RestController
@RequiredArgsConstructor
public class BoardCommentRestController {

	private final BoardCommentMapper bMapper;
	private final SimpMessagingTemplate template;
	private final NoticeProducer noticeProducer;
	
	public Map commonListData(int page,int board_no)
	{
		Map map = new HashMap();
		int start = (page*10)-10;
		map.put("start", start);
		map.put("board_no", board_no);
		
		List<BootCommentVO> list = bMapper.boardCommentListData(map);
		int count = bMapper.boardCommentCount(board_no);
		int totalpage = (int)(Math.ceil(count/10.0));
		
		map = new HashMap();
		map.put("list", list);
		map.put("curpage", page);
		map.put("totalpage", totalpage);
		map.put("count", count);

		return map;
	}
	@Async
	@GetMapping("/reply/list_vue")
	public ResponseEntity<Map> board_list(@RequestParam("board_no") int board_no, @RequestParam("page") int page)
	{
		Map map = new HashMap();
		try 
		{
			map = commonListData(page, board_no);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace(); 
			// 예전에는 return new ResponseEntity<> (null,HttpStatus.INTERNAL_SERVER_ERROR); 이렇게 작성했어
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
	
	@Async
	@PostMapping("/reply/insert_vue")
	public ResponseEntity<Map> reply_insert(@RequestBody BootCommentVO vo, HttpSession session)
	{
		Map map = new HashMap();
		try {
			String id = (String)session.getAttribute("userid");
			String name = (String)session.getAttribute("username");
			vo.setId(id);
			vo.setName(name);
			
			bMapper.boardCommentInsert(vo);
			map = commonListData(vo.getPage(), vo.getBoard_no());
			
		} catch (Exception ex) {
			ex.printStackTrace(); 
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
	
	// 대댓글 저장 -> 알림 보내기
	@PostMapping("/reply/rereply_insert_vue")
	public ResponseEntity<Map> rereply_insert(@RequestBody BootCommentVO vo, HttpSession session)
	{
		Map map = new HashMap();
		try {
			// 상위 댓글의 정보를 가져온다
			BootCommentVO pvo = bMapper.boardParentInfoData(vo.getNo());
			bMapper.boardGroupStepIncrement(pvo.getGroup_id(), pvo.getGroup_step());
			vo.setGroup_id(pvo.getGroup_id());
			vo.setGroup_step(pvo.getGroup_step()+1);
			vo.setGroup_tab(pvo.getGroup_tab()+1);
			vo.setRoot(vo.getNo());
			// 본인 => vo.setId
			vo.setId((String)session.getAttribute("userid"));
			vo.setName((String)session.getAttribute("username"));
			bMapper.boardCommentReReply(vo);
			bMapper.boardDepthIncrement(vo.getNo()); // 대댓글 인서트 끝
			
			// 대댓글 알림
			// 내 댓글에 내가 또 댓글을 단거 말고
			// pvo.getId() 여기에서 알림이 뜬다
			if(!pvo.getId().equals(vo.getId()))
			{
				// template.convertAndSend를 실행 시
				// => /sub/notice/"+pvo.getId()해당 주소로 메시지를 보내라고 브로커한테 요청
				
				// 카프카 이용하면 밑에 코드가 필요없어짐
				//template.convertAndSend("/sub/notice/"+pvo.getId(),
				// "[🔔댓글 알림🔔]"+vo.getId()+"님이 댓글을 달았습니다.");
				
				// chatMessage 속 내용이 여기 controller에서 채워진다(뭘 보낼지, 누구한테 보낼지에 대한 내용)
                ChatMessage notice = new ChatMessage(
                      vo.getId(), // sender
                      pvo.getId(), // receiver
                      "[🔔댓글 알림🔔]"+vo.getId()+"님이 댓글을 달았습니다."
                );		
                noticeProducer.sendNotice(notice);
				
			}
			
			map = commonListData(vo.getPage(), vo.getBoard_no());
			
		} catch (Exception ex) {
			ex.printStackTrace(); 
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
		
	}
}
