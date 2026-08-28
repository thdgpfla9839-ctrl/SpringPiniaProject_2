package com.sist.web.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 *   [실행과정]
 *   vue에서 댓글 작성
 *   |
 *   boardCommentRestController(url주소가 넘어감)
 *   |
 *   DB에 댓글 저장
 *   |
 *   전송(알림을 어떻게 보낼지)
 *   |
 *   알림생성
 *    => NoticeProducer
 *   |
 *   kafka
 *    => notice-topic
 *   |
 *   NoticeConsumer
 *   |
 *   SimpMessageTemplate
 *   |
 *   STOMP
 *   |
 *   vue에 boardStore.js로 넘어감
 *   |
 *   showToast
 */


/*
 *   [카프카에서 사용하는 용어]
 *   Producer : 카프카에서 메시지를 보는 것
 *   Consumer : 카프카에서 메시지를 읽는다
 */

@Data
@AllArgsConstructor // 매개변수가 있는 생성자
@NoArgsConstructor
public class ChatMessage {
    private String sender; // 보낸사람
    private String receiver; // 받는 사람
    private String message; // 채팅 메시지
}