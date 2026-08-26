package com.sist.web.manager;
// 여기는 일반 클래스 => task를 사용할 땐 저장할 곳을 정해야해
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// 실시간 검색어 실시간으로 가져와서 뿌리기
@Component
public class RealFindWordTask {

	@Async // 크롤링하는 거랑 비동기적으로 처리해줘야 속도가 빨라짐 그래서 비동기로 만들어주는 중 
	@Scheduled(fixedRate = 60*3*1000) // 3분마다
	public void task()
	{
		
	}
}
