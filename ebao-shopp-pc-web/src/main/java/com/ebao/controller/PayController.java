package com.ebao.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.feign.PayServiceFeign;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PayController {

	@Autowired
	private PayServiceFeign payServiceFeign;
	//ʹ��token��֧��
	@RequestMapping("/aliPay")
	public void aliPay(String payToken,HttpServletResponse response) throws IOException{
		response.setContentType("text/html;charset=utf-8");
		PrintWriter writer = response.getWriter();
		//1��������֤
		if (StringUtils.isEmpty(payToken)) {
			return ;
		}
		//2������֧������ӿڻ�ȡ֧����htmlԪ��
		ResponseBase payTokenResult = payServiceFeign.findPayToken(payToken);
		if (!Constants.HTTP_RES_CODE_200.equals(payTokenResult.getRtnCode())) {
			String msg = payTokenResult.getMsg();
			writer.println(msg);
			return ;
		}
		//3�����ؿ�ִ�е�htmlԪ�ظ��ͻ���
		@SuppressWarnings("rawtypes")
		LinkedHashMap data = (LinkedHashMap) payTokenResult.getData();
		String payHtml = (String) data.get("payHtml");
		log.info("#####PayController###payHtml:{}",payHtml);
		//4��ҳ������Ⱦ����
		writer.println(payHtml);
		writer.close();
		
	}
}
