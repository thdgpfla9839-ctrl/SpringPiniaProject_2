package com.sist.web.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.sist.web.vo.*;

import lombok.RequiredArgsConstructor;
// 카프카를 읽어서 stomp으로 메시지를 브라우저에 전송하는 곳
@Service
@RequiredArgsConstructor
/*
 *  NoticeProducer에서 sendNotice( ) 실행
 *  둘이 notice-topic이 같으면
 *  NoticeConsumer에서 consumerNotice( ) 호출
 *  
 *  
 *  
 *  send() => topic => @kafkaListner - convertandsend()-subscribe()
 *  
 *  subscribe() : 데이터를 받는 곳
 *  que : 메시지를 저장하는 공간 => notice-topic에 해당됨
 *  
 *  1) User => 브라우저에서 요청을 보냄
 *  2) Controller에서 요청 받기
 *  3) kafka로 메시지 보내기 => 메시지를 보내기 전 메시지를 보낼 클래스를 producer에서 생성 후 send()
 *  4) Cunsumer에서 메시지 받기 => @kafkaListenr부분
 *  5) 값을 받으면 브라우저로 데이터를 보낸다
 *     => template.convertAndSend(dest, notice.getMessage)
 *  6) 데이터를 받아서 출력
 *     => this.stomp.subscribe('/sub/notice/'+id,msg=>{ 
			this.showToast(msg.body)
			this.boardCommentListData(this.board_no)   
 */


public class NoticeConsumer {

	// 얘를 통해 pinia로 stomp를 이용한 메시지를 보낸다
	private final SimpMessagingTemplate template;
    
	@KafkaListener(
	    topics = "notice-topic", // 이 부분은 producer에서 생성한 키와 맞춰야함
	    groupId="notice-group" // 이거는 consumer Group에 해당되는데 이름은 자유롭게 줘도 됨 => 해당 그룹에 해당되는 부분만 처리할 거야
	)
	public void consumerNotice(ChatMessage notice)
	{
		// kafka에서 메시지가 들어오면 => 스프링에서 해당 메소드(consumerNotice()) 자동 호출
		System.out.println("kafka 알림 수신: "+notice);
		// 누구한테 보낼건지 확정
		String dest = "/sub/notice/"+notice.getReceiver();
		template.convertAndSend(
		   // 누구한테 보낼지
				dest, 
		   // 뭘 보낼지
		   notice.getMessage()
        );
		// Stomp을 이용해서 vue로 다시 전송하는 부분
		System.out.println("STOMP 알림 전송완료: "+dest);
	}
}
