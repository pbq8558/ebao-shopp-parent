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

	// 跳转注册页面
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String register() {
		return REGISTER;
	}

	// 注册业务具体实现
	@RequestMapping(value = "/doRegister", method = RequestMethod.POST)
	public String doRegister(UserEntity userEntity,HttpServletRequest request) {
		
		//1、验证参数,此处省略。。。
		//2、调用会员注册接口
		ResponseBase responseBase = memberServiceFeign.regUser(userEntity);
		//3、如果失败，跳转到失败页面
		if (!responseBase.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
			request.setAttribute("error", "注册失败！");
		}
		//4、如果成功，跳转到成功页面
		return LOGIN;
	}

}
