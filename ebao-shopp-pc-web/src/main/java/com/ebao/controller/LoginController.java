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

	// ��ת����¼ҳ��
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginGet() {
		return LOGIN;
	}

	// ��½ҳ���ύ�ľ���ʵ��
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost(UserEntity userEntity, HttpServletRequest request, HttpServletResponse response) {
		// 1����֤����,ʡ�ԡ�����
		// 2�����õ�¼�ӿڣ���ȡtoken��Ϣ
		ResponseBase loginBase = memberServiceFeign.login(userEntity);
		if (!Constants.HTTP_RES_CODE_200.equals(loginBase.getRtnCode())) {
			request.setAttribute("error", "�˺Ż��������");
			return LOGIN;
		}
		//��ʱ��ȡ����loginBase�е�data��LinkedHashMap���͵Ķ�����JSONObject���͵ģ�����ᱨ������
		@SuppressWarnings("rawtypes")
		LinkedHashMap loginData = (LinkedHashMap) loginBase.getData();
		String memberToken = (String) loginData.get("memberToken");
		if (StringUtils.isEmpty(memberToken)) {
			request.setAttribute("error", "�Ự�Ѿ�ʧЧ��");
		}
		// 3����token��Ϣ�����cookie��
		setCookie(memberToken,response);
		return INDEX;
	}
	
	public void setCookie(String memberToken, HttpServletResponse response){
		CookieUtil.addCookie(response, Constants.COOKIE_MEMBER_TOKEN, memberToken, Constants.COOKIE_MEMBER_TIMEOUT);
	}
	
	@RequestMapping("/localQQLogin")
	public String localQQLogin(HttpServletRequest request) throws QQConnectException{
		//������Ȩ����
		String authorizeURL = new Oauth().getAuthorizeURL(request);
		//�ض�����Ѷ����Ȩҳ��
		return "redirect:"+authorizeURL;
	}

	@RequestMapping("/qqLoginCallback")
	public String qqLoginCallback(HttpServletRequest request,HttpSession session, HttpServletResponse response) throws QQConnectException{
		//1.��ȡ��Ȩ��code
		
		//2.ʹ����Ȩ��code��ȡaccessToken
		AccessToken accessTokenObj = new Oauth().getAccessTokenByRequest(request);
		if (accessTokenObj == null) {
			//��Ȩʧ��
			request.setAttribute("error", "QQ��Ȩʧ��");
			return "error";
		}
		String accessToken = accessTokenObj.getAccessToken();
		if (accessToken == null) {
			request.setAttribute("error", "accessTokenΪnull");
			return "error";
		}
		//3.ʹ��accessToken��ȡopenid
		OpenID openIdObj = new OpenID(accessToken);
		String openid = openIdObj.getUserOpenID();
		//4.���û�Ա����ӿڡ�ʹ��openid �����Ƿ��Ѿ������˺�
		ResponseBase openUserBase = memberServiceFeign.findByOpenIdUser(openid);
		if (Constants.HTTP_RES_CODE_201.equals(openUserBase.getRtnCode())) {
			//5.���û�й����˺ţ���ת�������˺�ҳ��
			session.setAttribute("qqOpenid", openid);
			return QQRELATION;
		}
		//6.�Ѿ����˺� �Զ���¼ �����û���Token��Ϣ�����Cookie��
		@SuppressWarnings("rawtypes")
		LinkedHashMap dataToken = (LinkedHashMap) openUserBase.getData();
		String memberToken = (String) dataToken.get("memberToken");
		setCookie(memberToken,response);
		return INDEX;
	}
	
	@RequestMapping(value = "/qqRelation", method = RequestMethod.POST)
	public String qqRelation(UserEntity userEntity, HttpServletRequest request,HttpSession session, HttpServletResponse response) {
		// 1����ȡopenid
		String openid = (String) session.getAttribute("qqOpenid");
		if (StringUtils.isEmpty(openid)) {
			request.setAttribute("error", "û�л�ȡ��openid");
			return "error";
		}
		userEntity.setOpenid(openid);
		// 2�����õ�¼�ӿڣ���ȡtoken��Ϣ
		ResponseBase loginBase = memberServiceFeign.qqLogin(userEntity);
		if (!Constants.HTTP_RES_CODE_200.equals(loginBase.getRtnCode())) {
			request.setAttribute("error", "�˺Ż��������");
			return LOGIN;
		}
		//��ʱ��ȡ����loginBase�е�data��LinkedHashMap���͵Ķ�����JSONObject���͵ģ�����ᱨ������
		@SuppressWarnings("rawtypes")
		LinkedHashMap loginData = (LinkedHashMap) loginBase.getData();
		String memberToken = (String) loginData.get("memberToken");
		if (StringUtils.isEmpty(memberToken)) {
			request.setAttribute("error", "�Ự�Ѿ�ʧЧ��");
		}
		// 3����token��Ϣ�����cookie��
		setCookie(memberToken,response);
		return INDEX;
	}
}
