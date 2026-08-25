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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Controller
@RequiredArgsConstructor
public class ChatController {

	/*
	 *    1. 사용자가 채팅페이지 접속 
	 *    2. Spring Security가 로그인 사용자 확인 
	 *       => <li sec:authorize="isAuthenticated()"><a href="/chat/chat">실시간 채팅</a></li>
	 *              -------------------------------- 로그인 여부 확인 
	 *    3. ThymeLeaf => LOGIN_USER 생성 : 로그인시 아이디 저장 
	 *        <script th:inline="javascript">
	           const LOGIN_USER =
	            [[${session.userid}]] '';
	           </script>
	      4. Vue.createApp()
	      5. Pinia등록 
	      6. useChatStore() 
	      7. onMounted()
	               store.loginUser = 사용자 아이디 저장 
	                      LOGIN_USER

	                store.chatBodyEl = 채팅 Form => DOM
	                    chatBody.value
	                     => private / public 

	                store.connect()
	      8. SockJS 연결 
	      8-1. STOMP 연결 => this.stomp =
	                         Stomp.over(socket)
	      9. 서버 채팅 : destination subscribe
	         채널 => 출력 위치 설정 
	      10. 실시간 메세지 대기 
	             |
	           store.msg => Enter => store.send()
	            |
	           STOMP SEND 
	            |
	           WebSocket에서 처리 
	            |
	           상대방 / 전체 메세지 전송 
	            |
	           STOMP => Message 수신 
	            |
	           store.message에 추가 
	            |
	           Vue수행 => 화면에 출력 
	                
	         
	 */
	
	
	// STOMP => 서버에서 특정한 클라이언트에세 메시지를 전송해주는 역할의 클래스 => 1:1, 알림 => 사용할 때는 반드시 id를 포함해서 누구한테 보내는지 지정해야함
    private final SimpMessagingTemplate template;
    // 접속자 저장 공간 
    // Set => 여러 쓰레드에서 동시에 안전하게 사용할 수 있게 만든다
    // 쓰레드는 중복을 제거해서 관리하는 역할을 함 => 웹소캣을 이용한 채팅에서 사용자 정보를 저장할 때 주로 사용하는 클래스
    private final Set<String> users=
    		ConcurrentHashMap.newKeySet();
    @MessageMapping("/chat/public")
    // => HttpSession을 포함하면 안된다 (GetMapping)
    // @SendTo : 전체 채팅을 만들어줌 => topic => 전체로 사용한다는 의미
    @SendTo("/topic/chat")
    // 일반 채팅은 msg를 보내서 msg: my: you: 메시지를 보낼때마다 :을 붙인다
    public ChatMessage publicChat(
            ChatMessage msg,
            Principal p) {

    	// 여기서는 session을 사용하지 못하므로 Spring Security => Principal 속 session 형식을 이용한다
        msg.setSender(p.getName());

        // /topic/chat => 모든 접속자에게 전송
        return msg;
    }

    // 여기는 1:1 채팅 부분
    @MessageMapping("/chat/private")
    public void privateChat(
            ChatMessage msg,
            Principal p) {

    	// sender : 현재 로그인된 사용자의 ID를 가져온다
        String sender = p.getName();

        // 서버에서 보낸 사람을 지정
        msg.setSender(sender);

        template.convertAndSendToUser(
                msg.getReceiver(), // 특정한 사람에게 보내는 중(상대방한테 메시지 전송)
                "/queue/chat",
                msg
        );

        // 일대일 채팅은 상대방, 본인한테 두번을 메시지 전송을 해줘야 한다
        template.convertAndSendToUser(
                sender,
                "/queue/chat",
                msg
        );
    }
    // 사용자 목록을 전송
    @MessageMapping("/chat/join")
    public void join(Principal p) // Principal p => Security에 저장돼 있는 ID
    {
    	String username=p.getName();
    	users.add(username);
    	template.convertAndSend(
    			"/topic/users", // topic이 나왔으니 전체에게 전송하겠찌
    			users
    	);
    }
    // 화면이동 => 원래는 RouterController에 있어야 하는데 지금은 연습이라 그냥 작성함
    @GetMapping("/chat/chat")
    public String chat_page(Model model) {
        model.addAttribute("main_html","chat.chat");
    	return "main/main";
    }
}