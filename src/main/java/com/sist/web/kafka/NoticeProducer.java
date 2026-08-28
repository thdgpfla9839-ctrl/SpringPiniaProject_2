package com.sist.web.kafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sist.web.vo.*;

import lombok.RequiredArgsConstructor;
@Service // 메모리 할당을 하겠지
@RequiredArgsConstructor

// NoticeProducer가 하는 역할 => 전송할 메시지를 생성해주는 역할
public class NoticeProducer {

	// kafka로 데이터를 전송하는 객체이름
	private final KafkaTemplate<String, ChatMessage> kafkaTemplate;
    // 그룹 이름 설정
	private static final String TOPIC="notice-topic";
	
	// 이 메소드는 컨트롤러에서 호출한다
	public void sendNotice(ChatMessage notice)
	{
		// ChatMessage의 객체(notice) 보내기
		kafkaTemplate.send(
				TOPIC, // 구분자 => notice-topic으로 보낼듯
				notice.getReceiver(), // 메시지를 받는 사람이 누구인지 예를들어 shin이라고 보냄
				notice // 메시지까지 포함
				);
		
		
		/*
		 * ChatMessage를 알고 있는 걔 => boardSore.js에서 /reply/rereply_insert_vue 이 부분에서 시작
		 * Controller 거치고
		 * kafkaTemplate.send()속 문장이 실행해서 전송
		 * 
		 * [출력 순서]
		 * vue => 댓글 입력이 되면
		 * |
		 * RestController에서 chatMessage 실행
		 * |
		 * Kafka => Producer
		 * |
		 * notice-topic
		 * |
		 * Consumer에서 Stomp을 통해 => 다시 Vue로 전송
		 *  
		 */
		
		
		
		// 제대로 넘어갔는지 확인차
		System.out.println("카프카 알림"+notice);
	}
	
	
	
	
	
	
	
	
	
	/*
	 *    User가 Pinia로 전송
	 *    Controller에서 send( ) 호출
	 *    kafka가 notice-topic을 수행
	 *    받아서 처리하는 역할이 Consumer
	 * 
	 */
}
