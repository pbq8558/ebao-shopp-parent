package com.ebao.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseMessage {

	/**
	 * ������΢��
	 */
	private String ToUserName;
	/**
	 * ���ͷ�openid
	 */
	private String FromUserName;
	/**
	 * ����ʱ��
	 */
	private long CreateTime;
	/**
	 * ��������
	 */
	private String MsgType;
	// /**
	// * ��Ϣid
	// */
	// private long MsgId ;
}

