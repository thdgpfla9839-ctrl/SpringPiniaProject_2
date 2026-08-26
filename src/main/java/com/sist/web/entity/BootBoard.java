package com.sist.web.entity;

import java.time.LocalDateTime;
// JPA(java persistence api)
// => java orm(object Relation Mapping => 객체기반)의 표준
// orm => 관계형 데이터베이스 
//     => 자동으로 sql 문장을 만든다
//     => 검색을 할 때는 findBy => WHERE 컬럼 연산자 값 순으로 됨
//     => 예) 컬럼 : no /   연산자 : = / 값 : 1
//     =>     findByNo(int no) 
// orm은 자바객체와 데이터베이스 컬럼 맵핑과 매칭 => 반드시 객체와 컬럼이 동일해야함
// insert / update / delete를 자체 내에서 만들다 보니 동일하게 줘야함
// Entity를 선언하게 되면 반드시 컬럼과 일치해야함
// save () / delete ()를 이용한다
// insert / update는 save()를 이용한다
// ============================================================
// 장단점
// 1) sql 의존도 감소 => 객체 중심 개발
// 2) crud 중심 => 개발이 빠름
// 3) 캐시메모리나 지연로딩을 이용 => 성능 최적화
// 4) 복잡한 객체관계가 있을 때 => 이해가 어렵다 => join같은 경우
// 5) 서브쿼리를 지원하지 X
// 6) join 시 잘못 설정하면 성능 저하 ↓ => N:1인지 N:N인지 잘 확인해야함
// =============================================================
// 생명주기)
// JPA에서 연결 => 메소드 호출 => SQL 문장 제작 => DB 연동
// =============================================================
// => 주로 간단한 CRUD 제작에 JPA를 사용
// => 대용량의 작업은 => MyBatis를 이용
// =============================================================
// MyBatis 와 JPA의 차이와 장단점 => 이런 부분이 면접으로 자주 나옴

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.Persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/*
 *  NO      NOT NULL NUMBER         
	NAME    NOT NULL VARCHAR2(51)   
	SUBJECT NOT NULL VARCHAR2(4000) 
	CONTENT NOT NULL CLOB           
	PWD     NOT NULL VARCHAR2(10)   
	REGDATE          DATE           
	HIT              NUMBER   
 */
@Entity // 데이터베이스 컬럼하고 연결
@Table(name="bootboard") // 여기서는 대소문자 구분하지 말고 무조건 소문자로 줘야함 => 테이블명
@DynamicUpdate
@Data
// 여기는 sql 문장을 자동으로 만들어주니까 pk의 중복 방지를 위해?
public class BootBoard {

	@Id
	// 자동 증가 번호 설정을 위해 => 자체 내에 시퀀스 이용
	// @GeneratedValue(strategy = GenerationType.SEQUENCE,generator="seq명")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int no;
	private String name;
	private String subject;
	private String content;
	@Column(insertable = true, updatable = false)
	private String pwd;
	private int hit;
	@Column(insertable = true, updatable = false, name="regdate")
	private LocalDateTime regdate;
	
	@PrePersist
	public void perSist()
	{
		// 오늘 날짜 가져오는 형식
		regdate = LocalDateTime.now();
	}
}
