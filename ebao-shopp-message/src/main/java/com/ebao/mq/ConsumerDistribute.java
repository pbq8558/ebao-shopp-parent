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
	//监听消息的方法
	@JmsListener(destination="messages_queue")
	public void distribute(String json){
		log.info("########消息服务平台接收消息内容：########",json);
		if (StringUtils.isEmpty(json)) {
			return;
		}
		//将字符串转换成json对象形式
		@SuppressWarnings("static-access")
		JSONObject rootJSON = new JSONObject().parseObject(json);
		JSONObject header = rootJSON.getJSONObject("header");
		String interfaceType = header.getString("interfaceType");
		
		if (StringUtils.isEmpty(interfaceType)) {
			return;
		}
		//判断接口类型是否是发邮件
		if (interfaceType.equalsIgnoreCase(Constants.MSG_EMAIL)) {
			//调用发邮件接口
			messageAdapter = emailService;
		}
		if (messageAdapter == null) {
			return;
		}
		JSONObject contentJson = rootJSON.getJSONObject("content");
		messageAdapter.sendMsg(contentJson);
	}
}
