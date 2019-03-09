package com.ebao.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.ebao.adapter.MessageAdapter;
import com.ebao.constants.Constants;
import com.ebao.email.service.EmailService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ConsumerDistribute {

	@Autowired
	private EmailService emailService;
	private MessageAdapter messageAdapter;
	//������Ϣ�ķ���
	@JmsListener(destination="messages_queue")
	public void distribute(String json){
		log.info("########��Ϣ����ƽ̨������Ϣ���ݣ�########",json);
		if (StringUtils.isEmpty(json)) {
			return;
		}
		//���ַ���ת����json������ʽ
		@SuppressWarnings("static-access")
		JSONObject rootJSON = new JSONObject().parseObject(json);
		JSONObject header = rootJSON.getJSONObject("header");
		String interfaceType = header.getString("interfaceType");
		
		if (StringUtils.isEmpty(interfaceType)) {
			return;
		}
		//�жϽӿ������Ƿ��Ƿ��ʼ�
		if (interfaceType.equalsIgnoreCase(Constants.MSG_EMAIL)) {
			//���÷��ʼ��ӿ�
			messageAdapter = emailService;
		}
		if (messageAdapter == null) {
			return;
		}
		JSONObject contentJson = rootJSON.getJSONObject("content");
		messageAdapter.sendMsg(contentJson);
	}
}
