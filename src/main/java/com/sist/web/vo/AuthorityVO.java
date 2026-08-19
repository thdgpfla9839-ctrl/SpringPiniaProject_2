package com.sist.web.vo;

import lombok.Data;

/*
 *  USERID             VARCHAR2(20) 
	AUTHORITY NOT NULL VARCHAR2(20) 
 */
@Data
// 권한에서 값을 집어넣을 떄는 ROLE_ADMIN 이렇게 줘야한다
public class AuthorityVO {

	private String userid;
	private String authority;
}
