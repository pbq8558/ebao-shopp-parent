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
	 * signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
	 * timestamp 时间戳 nonce 随机数 echostr 随机字符串
	 * 
	 * @return
	 */
	// 微信公众平台服务器验证的接口地址
	@RequestMapping(value = "/dispatcher", method = RequestMethod.GET)
	public String dispatcherGet(String signature, String timestamp, String nonce, String echostr) {
		// 1.验证参数
		boolean flag = CheckUtil.checkSignature(signature, timestamp, nonce);
		// 2.参数验证成功之后,返回随机数
		if (!flag) {
			return null;
		}

		return echostr;
	}

	// 微信公众平台动作推送
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/dispatcher", method = RequestMethod.POST)
	public void dispatcherPOST(HttpServletRequest request,HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// 1.将XML转换成Map格式
		Map<String,String> resultMap = XmlUtils.parseXml(request);
		log.info("#####receiveWechatMessage#####resultMap" + resultMap.toString());
		// 2.判断消息类型
		String msgType = resultMap.get("MsgType");
		// 3.如果是文本类型，返回结果给微信服务器端
		PrintWriter writer = response.getWriter();
		switch (msgType) {
		case "text":
			//开发者的微信公众号
			String toUserName = resultMap.get("ToUserName");
			//消息来自何方
			String fromUserName = resultMap.get("FromUserName");
			//消息内容
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
				msg = setText("我现在有点慢,稍后回复你",toUserName,fromUserName);
			}
			/*if ("易保科技".equals(content)) {
				//返回相关信息，此处from和to要反过来
				msg = setText("易保科技官方网址：www.ebaoTech.com",toUserName,fromUserName);
				
			}else{
				//返回一段话
				msg = setText("我不知道该说啥",toUserName,fromUserName);
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
