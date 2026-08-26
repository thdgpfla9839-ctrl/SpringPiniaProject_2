package com.sist.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // task 사용시 반드시 추가
@EnableAspectJAutoProxy // 이렇게 줘여 aop가 작동한다
public class SpringPiniaProject2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringPiniaProject2Application.class, args);
	}

}
