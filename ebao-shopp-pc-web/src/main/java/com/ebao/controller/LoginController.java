package com.ebao.controller;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.entity.UserEntity;
import com.ebao.feign.MemberServiceFeign;
import com.ebao.utils.CookieUtil;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.oauth.Oauth;

@Controller
public class LoginController {

	@Autowired
	private MemberServiceFeign memberServiceFeign;

	private static final String LOGIN = "login";

	private static final String INDEX = "redirect:/";
	
	private static final String QQRELATION = "qqrelation";

	// 跳转到登录页面
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginGet() {
		return LOGIN;
	}

	// 登陆页面提交的具体实现
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost(UserEntity userEntity, HttpServletRequest request, HttpServletResponse response) {
		// 1、验证参数,省略。。。
		// 2、调用登录接口，获取token信息
		ResponseBase loginBase = memberServiceFeign.login(userEntity);
		if (!Constants.HTTP_RES_CODE_200.equals(loginBase.getRtnCode())) {
			request.setAttribute("error", "账号或密码错误");
			return LOGIN;
		}
		//此时获取到的loginBase中的data是LinkedHashMap类型的而不是JSONObject类型的，否则会报错！！！
		@SuppressWarnings("rawtypes")
		LinkedHashMap loginData = (LinkedHashMap) loginBase.getData();
		String memberToken = (String) loginData.get("memberToken");
		if (StringUtils.isEmpty(memberToken)) {
			request.setAttribute("error", "会话已经失效！");
		}
		// 3、将token信息存放在cookie中
		setCookie(memberToken,response);
		return INDEX;
	}
	
	public void setCookie(String memberToken, HttpServletResponse response){
		CookieUtil.addCookie(response, Constants.COOKIE_MEMBER_TOKEN, memberToken, Constants.COOKIE_MEMBER_TIMEOUT);
	}
	
	@RequestMapping("/localQQLogin")
	public String localQQLogin(HttpServletRequest request) throws QQConnectException{
		//生成授权链接
		String authorizeURL = new Oauth().getAuthorizeURL(request);
		//重定向到腾讯的授权页面
		return "redirect:"+authorizeURL;
	}

	@RequestMapping("/qqLoginCallback")
	public String qqLoginCallback(HttpServletRequest request,HttpSession session, HttpServletResponse response) throws QQConnectException{
		//1.获取授权码code
		
		//2.使用授权码code获取accessToken
		AccessToken accessTokenObj = new Oauth().getAccessTokenByRequest(request);
		if (accessTokenObj == null) {
			//授权失败
			request.setAttribute("error", "QQ授权失败");
			return "error";
		}
		String accessToken = accessTokenObj.getAccessToken();
		if (accessToken == null) {
			request.setAttribute("error", "accessToken为null");
			return "error";
		}
		//3.使用accessToken获取openid
		OpenID openIdObj = new OpenID(accessToken);
		String openid = openIdObj.getUserOpenID();
		//4.调用会员服务接口。使用openid 查找是否已经关联账号
		ResponseBase openUserBase = memberServiceFeign.findByOpenIdUser(openid);
		if (Constants.HTTP_RES_CODE_201.equals(openUserBase.getRtnCode())) {
			//5.如果没有关联账号，跳转到关联账号页面
			session.setAttribute("qqOpenid", openid);
			return QQRELATION;
		}
		//6.已经绑定账号 自动登录 并将用户的Token信息存放在Cookie中
		@SuppressWarnings("rawtypes")
		LinkedHashMap dataToken = (LinkedHashMap) openUserBase.getData();
		String memberToken = (String) dataToken.get("memberToken");
		setCookie(memberToken,response);
		return INDEX;
	}
	
	@RequestMapping(value = "/qqRelation", method = RequestMethod.POST)
	public String qqRelation(UserEntity userEntity, HttpServletRequest request,HttpSession session, HttpServletResponse response) {
		// 1、获取openid
		String openid = (String) session.getAttribute("qqOpenid");
		if (StringUtils.isEmpty(openid)) {
			request.setAttribute("error", "没有获取到openid");
			return "error";
		}
		userEntity.setOpenid(openid);
		// 2、调用登录接口，获取token信息
		ResponseBase loginBase = memberServiceFeign.qqLogin(userEntity);
		if (!Constants.HTTP_RES_CODE_200.equals(loginBase.getRtnCode())) {
			request.setAttribute("error", "账号或密码错误");
			return LOGIN;
		}
		//此时获取到的loginBase中的data是LinkedHashMap类型的而不是JSONObject类型的，否则会报错！！！
		@SuppressWarnings("rawtypes")
		LinkedHashMap loginData = (LinkedHashMap) loginBase.getData();
		String memberToken = (String) loginData.get("memberToken");
		if (StringUtils.isEmpty(memberToken)) {
			request.setAttribute("error", "会话已经失效！");
		}
		// 3、将token信息存放在cookie中
		setCookie(memberToken,response);
		return INDEX;
	}
}
