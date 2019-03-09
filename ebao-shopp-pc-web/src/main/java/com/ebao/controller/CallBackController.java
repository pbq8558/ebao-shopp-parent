package com.ebao.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.feign.CallBackServiceFeign;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/alibaba/callBack")
public class CallBackController {
	@Autowired
	private CallBackServiceFeign callBackServiceFeign;
	private String PAY_SUCCESS = "pay_success";

	// ͬ��֪ͨ
	@RequestMapping("/return_url")
	public void synCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.info("#####֧����ͬ���ص�CallBackController###synCallBack##start");
		response.setContentType("text/html;charset=utf-8");
		PrintWriter writer = response.getWriter();
		// ��ȡ֧����GET����������Ϣ
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			// ����������δ����ڳ�������ʱʹ��
			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		ResponseBase syncCallBackResponseBase = callBackServiceFeign.synCallBack(params);
		if (!Constants.HTTP_RES_CODE_200.equals(syncCallBackResponseBase.getRtnCode())) {
			// ����ҳ��
			return ;
		}
		@SuppressWarnings("rawtypes")
		LinkedHashMap data = (LinkedHashMap) syncCallBackResponseBase.getData();
		// �̻�������
		String outTradeNo = (String) data.get("outTradeNo");
		// ֧�������׺�
		String tradeNo = (String) data.get("tradeNo");
		// ������
		String totalAmount = (String) data.get("totalAmount");
		//��װ��html�������ģ���ύ
		String htmlFrom = "<form name='punchout_form'"
				+ " method='post' action='http://127.0.0.1/alibaba/callBack/synSuccessPage' >"
				+ "<input type='hidden' name='outTradeNo' value='" + outTradeNo + "'>"
				+ "<input type='hidden' name='tradeNo' value='" + tradeNo + "'>"
				+ "<input type='hidden' name='totalAmount' value='" + totalAmount + "'>"
				+ "<input type='submit' value='����֧��' style='display:none'>"
				+ "</form><script>document.forms[0].submit();" + "</script>";
		writer.println(htmlFrom);
	}

	// ��post����ʽ���ر����ݲ���
	@RequestMapping(value = "/synSuccessPage", method = RequestMethod.POST)
	public String syncSuccessPage(HttpServletRequest request, String outTradeNo, String tradeNo, String totalAmount) {
		request.setAttribute("outTradeNo", outTradeNo);
		request.setAttribute("tradeNo", tradeNo);
		request.setAttribute("totalAmount", totalAmount);
		return PAY_SUCCESS;
	}

	// �첽֪ͨ
	@RequestMapping("/notify_url")
	@ResponseBody
	public String asynCallBack(HttpServletRequest request, HttpServletResponse response){
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		String result = callBackServiceFeign.asynCallBack(params);
		
		return result;
	}
}
