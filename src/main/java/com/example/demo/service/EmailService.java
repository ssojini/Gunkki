package com.example.demo.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService
{
	@Autowired
	private JavaMailSender sender;
	@Autowired
	private HttpSession session;	 
	
	//유일한 문자를 만드는 메소드
	private String createRandomStr()
	{
		UUID randomUUID = UUID.randomUUID();
		return randomUUID.toString().replaceAll("-", "");
	}	
	
	/* ----------------- 상욱 시작 ----------------- */

	public boolean sendSimpleText()
	{
		List<String> receivers = new ArrayList<>();
		receivers.add("siesta_w@naver.com");

		String[] arrReceiver = (String[])receivers.toArray(new String[receivers.size()]);

		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

		simpleMailMessage.setTo(arrReceiver); //수신자 설정

		simpleMailMessage.setSubject("Spring Boot Mail Test");
		simpleMailMessage.setText("스프링에서 메일 보내기 테스트");
		//SimpleMailMessage를 사용하여 html 을 전달하더라도 수신자의 화면에는 html이 해석되지 않음
		simpleMailMessage.setText("<a href='/mail/auth/"+ createRandomStr()+"'>인증</a>");

      return false;
   }
   
	public boolean checkmail(HttpSession session)
	{
		//세션아이디 얻기
		String sid = session.getId();
		session.setAttribute("sid", sid);
		String email = (String) session.getAttribute("email");
		String rdStr =createRandomStr();
		session.setAttribute("rdStr", rdStr);

		MimeMessage mimeMessage = sender.createMimeMessage();

		try {
			InternetAddress[] addressTo = new InternetAddress[1];
			addressTo[0] = new InternetAddress(email);

			mimeMessage.setRecipients(Message.RecipientType.TO, addressTo);

			mimeMessage.setSubject("팀프로젝트 메일 확인");

			//로컬호스트로 테스트시 
			mimeMessage.setContent("<a href='http://localhost/team/auth/"+sid+"/"+rdStr+"'>메일주소 인증</a>", "text/html;charset=utf-8");
			//서버사용시 서버 IP주소 변경 할것
			//mimeMessage.setContent("<a href='http://192.168.0.92/team/auth/"+sid+"/"+rdStr+"'>메일주소 인증</a>", "text/html;charset=utf-8");
			
			
			sender.send(mimeMessage);
			return true;
		} catch (MessagingException e) {
			log.error("에러={}", e);
		}
		return false;
	}
	
	/* ----------------- 상욱 끝 ----------------- */
      
	
}