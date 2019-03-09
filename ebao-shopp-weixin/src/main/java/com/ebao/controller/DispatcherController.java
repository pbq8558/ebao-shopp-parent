package com.ebao.controller;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ebao.base.TextMessage;
import com.ebao.utils.CheckUtil;
import com.ebao.utils.HttpClientUtil;
import com.ebao.utils.XmlUtils;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
public class DispatcherController {
	
	private  static final String requestUrl = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=";

	/**
	 * signature ΢�ż���ǩ����signature����˿�������д��token�����������е�timestamp������nonce������
	 * timestamp ʱ��� nonce ����� echostr ����ַ���
	 * 
	 * @return
	 */
	// ΢�Ź���ƽ̨��������֤�Ľӿڵ�ַ
	@RequestMapping(value = "/dispatcher", method = RequestMethod.GET)
	public String dispatcherGet(String signature, String timestamp, String nonce, String echostr) {
		// 1.��֤����
		boolean flag = CheckUtil.checkSignature(signature, timestamp, nonce);
		// 2.������֤�ɹ�֮��,���������
		if (!flag) {
			return null;
		}

		return echostr;
	}

	// ΢�Ź���ƽ̨��������
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/dispatcher", method = RequestMethod.POST)
	public void dispatcherPOST(HttpServletRequest request,HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 1.��XMLת����Map��ʽ
		Map<String,String> resultMap = XmlUtils.parseXml(request);
		log.info("#####receiveWechatMessage#####resultMap" + resultMap.toString());
		// 2.�ж���Ϣ����
		String msgType = resultMap.get("MsgType");
		// 3.������ı����ͣ����ؽ����΢�ŷ�������
		PrintWriter writer = response.getWriter();
		switch (msgType) {
		case "text":
			//�����ߵ�΢�Ź��ں�
			String toUserName = resultMap.get("ToUserName");
			//��Ϣ���Ժη�
			String fromUserName = resultMap.get("FromUserName");
			//��Ϣ����
			String content = resultMap.get("Content");
			/*if (content.contains(" ")) {
				content = content.replace(" ", "%20");
			}*/
			content = URLEncoder.encode(content);
			String resultJson = HttpClientUtil.doGet(requestUrl + content);
			JSONObject jsonObject = JSONObject.parseObject(resultJson);
			Integer resultCode = jsonObject.getInteger("result");
			String msg = "";
			if (resultCode == 0) {
				String resultContent = jsonObject.getString("content");
				msg = setText(resultContent,toUserName,fromUserName);
			} else {
				msg = setText("�������е���,�Ժ�ظ���",toUserName,fromUserName);
			}
			/*if ("�ױ��Ƽ�".equals(content)) {
				//���������Ϣ���˴�from��toҪ������
				msg = setText("�ױ��Ƽ��ٷ���ַ��www.ebaoTech.com",toUserName,fromUserName);
				
			}else{
				//����һ�λ�
				msg = setText("�Ҳ�֪����˵ɶ",toUserName,fromUserName);
			}*/
			log.info("#####sendWechatMessage#####msg" + msg);
			writer.println(msg);
			break;

		default:
			break;
		}
		writer.close();
	}
	
	public String setText(String content,String fromUserName,String toUserName){
		TextMessage textMessage = new TextMessage();
		textMessage.setContent(content);
		textMessage.setCreateTime(new Date().getTime());
		textMessage.setFromUserName(fromUserName);
		textMessage.setToUserName(toUserName);
		textMessage.setMsgType("text");
		String msg = XmlUtils.messageToXml(textMessage);
		return msg;
	}
}
