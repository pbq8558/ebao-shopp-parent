package com.ebao.entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInfo {
	private Integer id;
	/**
	 * ֧������
	 */
	private Long typeId;
	/**
	 * �������
	 */
	private String orderId;
	/**
	 * ������ƽ̨֧��id
	 */
	private String platformorderId;
	/**
	 * �۸� �Է�Ϊ��λ
	 */
	private Long price;
	/**
	 * ֧����Դ
	 */
	private String source;
	/**
	 * ֧��״̬ 0 ��֧����1֧���ɹ� ��2֧��ʧ��
	 */
	private Integer state;
	/**
	 * ֧������
	 */
	private String payMessage;

	/**
	 * �û�userId
	 */
	private String userId;
	private Date created;
	private Date updated;

}