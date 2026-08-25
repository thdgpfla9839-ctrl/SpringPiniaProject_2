package com.sist.web.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sist.web.vo.ChatMessage;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate template;
    /*
     *   /topic/public 
     *   => 접속자 모든 사람에게 메세지 전송 
     *   => /user/{username}/queue/notify 
     *      => 1:1 채팅 
     *   => jackson을 이용해서 메세지를 JSON으로 자동화 처리 
     */
    // 전체 채팅
    @MessageMapping("/chat/public")
    @SendTo("/topic/chat")
    //Principal => Security에서 사용하는 session 데이터 
    public ChatMessage publicChat(ChatMessage msg,HttpSession session)
    {
    	// 로그인된 사용자 ID
    	msg.setSender((String)session.getAttribute("userid"));
    	// => id 
    	return msg;
    }
    // 1:1 채팅 
    @MessageMapping("/chat/private")
    public void privateChat(ChatMessage msg,HttpSession session)
    {
    	// 보내는 사람 
    	String sender=(String)session.getAttribute("userid");
    	msg.setSender(sender);
    	// 상대방 전송 
    	template.convertAndSendToUser(
    	  msg.getReceiver(),
    	  "/queue/chat",
    	  msg
    	);
    	// 본인 전송 
    	template.convertAndSendToUser(
    	    sender,
    	    "/queue/chat",
    	    msg
    	);
    }
    // 상대방에게 전송 
    // 본인에게 전송 
    // 채팅 페이지 이동 
    @GetMapping("/chat/chat")
    public String chat_chat(Model model) {
    	 model.addAttribute("main_html", "chat/chat");
    	 return "main/main";
    }
    // 알림 (X) 
}