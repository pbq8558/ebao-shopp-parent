package com.ebao.api.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebao.base.ResponseBase;
import com.ebao.entity.PaymentInfo;

@RequestMapping("pay")
public interface PayService {

	/**
	 * ����֧������
	 * @param paymentInfo
	 * @return
	 */
	@RequestMapping("/createToken")
	public ResponseBase createToken(@RequestBody PaymentInfo paymentInfo);
	
	/**
	 * ʹ��֧�����Ʋ���֧����Ϣ
	 * @param paymentInfo
	 * @return
	 */
	@RequestMapping("/findPayToken")
	public ResponseBase findPayToken(@RequestParam("token") String token);
}
