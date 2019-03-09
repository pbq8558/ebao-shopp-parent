package com.ebao.api.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebao.base.ResponseBase;
import com.ebao.entity.UserEntity;

@RequestMapping("/member")
public interface MemberService {

	// ʹ��userId�����û���Ϣ
	@RequestMapping("/findUserById")
	public ResponseBase findUserById(Long id);

	// @RequestBody���������Ϊjason��ʽ����ע��Ҫ�ڽӿں�ʵ�����ж�Ҫ��ӣ�������ղ��˲���
	@RequestMapping("/regUser")
	public ResponseBase regUser(@RequestBody UserEntity user);

	// �û���¼
	@RequestMapping("/login")
	public ResponseBase login(@RequestBody UserEntity user);

	// ʹ��token��¼
	@RequestMapping("/findByTokenUser")
	public ResponseBase findByTokenUser(@RequestParam("token") String token);

	// ʹ��OpenId��¼
	@RequestMapping("/findByOpenIdUser")
	public ResponseBase findByOpenIdUser(@RequestParam("openid") String openid);

	// ʹ��qq��¼
	@RequestMapping("/qqLogin")
	public ResponseBase qqLogin(@RequestBody UserEntity user);
}
