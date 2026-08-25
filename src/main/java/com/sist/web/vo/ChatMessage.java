package com.sist.web.vo;

import lombok.Data;
import lombok.Getter;

@Data
public class ChatMessage {
  private String sender;// 보낸사람 
  private String receiver; // 받는 사람 
  private String message; // 채팅 메세지 
}