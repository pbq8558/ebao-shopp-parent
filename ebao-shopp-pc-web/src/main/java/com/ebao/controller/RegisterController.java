package com.ebao.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.entity.UserEntity;
import com.ebao.feign.MemberServiceFeign;

@Controller
public class RegisterController {
	
	@Autowired
	private MemberServiceFeign memberServiceFeign;
	
	private static final String REGISTER = "register";

	private static final String LOGIN = "login";

	// ��תע��ҳ��
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register() {
		return REGISTER;
	}

	// ע��ҵ�����ʵ��
	@RequestMapping(value = "/doRegister", method = RequestMethod.POST)
	public String doRegister(UserEntity userEntity,HttpServletRequest request) {
		
		//1����֤����,�˴�ʡ�ԡ�����
		//2�����û�Աע��ӿ�
		ResponseBase responseBase = memberServiceFeign.regUser(userEntity);
		//3�����ʧ�ܣ���ת��ʧ��ҳ��
		if (!responseBase.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
			request.setAttribute("error", "ע��ʧ�ܣ�");
		}
		//4������ɹ�����ת���ɹ�ҳ��
		return LOGIN;
	}

}
