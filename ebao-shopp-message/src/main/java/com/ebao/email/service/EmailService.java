package com.ebao.email.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.ebao.adapter.MessageAdapter;

import lombok.extern.slf4j.Slf4j;

//��������������ʼ�
@Slf4j
@Service
public class EmailService implements MessageAdapter {
	
	@Value("${msg.subject}")
	private String subject;
	@Value("${msg.text}")
	private String text;
	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void sendMsg(JSONObject body) {
		//�������ʼ�
		String email = body.getString("email");
		if (StringUtils.isEmpty(email)) {
			return;
		}
		log.info("######SENDEMAIL START��",email);
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		//�˴��Լ����Լ�
		//�ʼ����ͷ��˺�
		simpleMailMessage.setFrom(email);
		//�ʼ����շ��˺�
		simpleMailMessage.setTo(email);
		//�ʼ�����
		simpleMailMessage.setSubject(subject);
		//�ʼ�����
		simpleMailMessage.setText(text.replace("{}", email));
		//�����ʼ�
		javaMailSender.send(simpleMailMessage);
		log.info("######SENDEMAIL END��",email);
	}

}
