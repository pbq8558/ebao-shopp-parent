package com.ebao.constants;

public interface Constants {
	// ��Ӧcode
	String HTTP_RES_CODE_NAME = "code";
	// ��Ӧmsg
	String HTTP_RES_CODE_MSG = "msg";
	// ��Ӧdata
	String HTTP_RES_CODE_DATA = "data";
	// ��Ӧ����ɹ�
	String HTTP_RES_CODE_200_VALUE = "success";
	// ϵͳ����
	String HTTP_RES_CODE_500_VALUE = "fail";
	// ��Ӧ����ɹ�code
	Integer HTTP_RES_CODE_200 = 200;
	// ϵͳ����
	Integer HTTP_RES_CODE_500 = 500;
	// δ����qq�˺�
	Integer HTTP_RES_CODE_201 = 201;
	// �����ʼ�
	String MSG_EMAIL = "EMAIL";
	// ��ԱToken
	String TOKEN_MEMBER = "TOKEN_MEMBER";
	// ֧��Token
	String TOKEN_PAY = "TOKEN_PAY";
	// ֧���ɹ�
	String PAY_SUCCESS = "success";
	// ֧��ʧ��
	String PAY_FAIL = "fail";
	// ��Ա��¼����Ч�ڣ�90�죩
	Long TOKEN_MEMBER_TIMEOUT = (long) (60 * 60 * 24 * 90);
	// ֧������Ч�ڣ�15���ӣ�
	Long PAY_TOKEN_TIMEOUT = (long) (60 * 15);
	// Cookie����Ч�ڣ�90�죩
	int COOKIE_MEMBER_TIMEOUT = (60 * 60 * 24 * 90);
	// Cookie ��ԱToken����
	String COOKIE_MEMBER_TOKEN = "COOKIE_MEMBER_TOKEN";
}
