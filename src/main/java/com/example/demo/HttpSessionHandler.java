package com.example.demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;

@WebListener
@SpringBootApplication
@Slf4j
public class HttpSessionHandler implements HttpSessionListener
{
	public static Map<String, HttpSession> map = new HashMap<>(); 
	
	public void sessionCreated(HttpSessionEvent se)
	{
		HttpSession s = se.getSession();
		map.put(s.getId(),s);
		System.err.println("session 생성");
		log.info("handler"+s.getId());
		
	}
	
	//세션이 사라지기 전에 할 것들을 아래 메소드에 정의한다
	public void sessionDestroyed(HttpSessionEvent se)
	{
		HttpSession s = se.getSession();
		map.remove(s.getId());
	}
}
