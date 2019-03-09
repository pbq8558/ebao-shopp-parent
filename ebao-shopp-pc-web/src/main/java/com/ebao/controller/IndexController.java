package com.ebao.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.feign.MemberServiceFeign;
import com.ebao.utils.CookieUtil;

//json格式
@Controller
public class IndexController {
	private static final String INDEX = "index";
	
	@Autowired
	private MemberServiceFeign memberServiceFeign;

	//主页
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(HttpServletRequest request){
		//1、从cookie中获取token信息
		String token = CookieUtil.getUid(request, Constants.COOKIE_MEMBER_TOKEN);
		//2、如果cookie中存在token
		if (!StringUtils.isEmpty(token)) {
			ResponseBase responseBase = memberServiceFeign.findByTokenUser(token);
			if (Constants.HTTP_RES_CODE_200.equals(responseBase.getRtnCode())) {
				@SuppressWarnings("rawtypes")
				LinkedHashMap userData = (LinkedHashMap) responseBase.getData();
				String userName = (String) userData.get("username");
				request.setAttribute("userName", userName);
			}
		}
		
		return INDEX;
	}
}
