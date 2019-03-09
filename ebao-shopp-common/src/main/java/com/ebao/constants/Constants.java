package com.ebao.constants;

public interface Constants {
	// 响应code
	String HTTP_RES_CODE_NAME = "code";
	// 响应msg
	String HTTP_RES_CODE_MSG = "msg";
	// 响应data
	String HTTP_RES_CODE_DATA = "data";
	// 响应请求成功
	String HTTP_RES_CODE_200_VALUE = "success";
	// 系统错误
	String HTTP_RES_CODE_500_VALUE = "fail";
	// 响应请求成功code
	Integer HTTP_RES_CODE_200 = 200;
	// 系统错误
	Integer HTTP_RES_CODE_500 = 500;
	// 未关联qq账号
	Integer HTTP_RES_CODE_201 = 201;
	// 发送邮件
	String MSG_EMAIL = "EMAIL";
	// 会员Token
	String TOKEN_MEMBER = "TOKEN_MEMBER";
	// 支付Token
	String TOKEN_PAY = "TOKEN_PAY";
	// 支付成功
	String PAY_SUCCESS = "success";
	// 支付失败
	String PAY_FAIL = "fail";
	// 会员登录的有效期（90天）
	Long TOKEN_MEMBER_TIMEOUT = (long) (60 * 60 * 24 * 90);
	// 支付的有效期（15分钟）
	Long PAY_TOKEN_TIMEOUT = (long) (60 * 15);
	// Cookie的有效期（90天）
	int COOKIE_MEMBER_TIMEOUT = (60 * 60 * 24 * 90);
	// Cookie 会员Token名称
	String COOKIE_MEMBER_TOKEN = "COOKIE_MEMBER_TOKEN";
}
