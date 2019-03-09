package com.ebao.api.service;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebao.base.ResponseBase;
import com.ebao.entity.UserEntity;

@RequestMapping("/member")
public interface MemberService {

	// 使用userId查找用户信息
	@RequestMapping("/findUserById")
	public ResponseBase findUserById(Long id);

	// @RequestBody接收茹入参为jason格式，该注解要在接口和实现类中都要添加，否则接收不了参数
	@RequestMapping("/regUser")
	public ResponseBase regUser(@RequestBody UserEntity user);

	// 用户登录
	@RequestMapping("/login")
	public ResponseBase login(@RequestBody UserEntity user);

	// 使用token登录
	@RequestMapping("/findByTokenUser")
	public ResponseBase findByTokenUser(@RequestParam("token") String token);

	// 使用OpenId登录
	@RequestMapping("/findByOpenIdUser")
	public ResponseBase findByOpenIdUser(@RequestParam("openid") String openid);

	// 使用qq登录
	@RequestMapping("/qqLogin")
	public ResponseBase qqLogin(@RequestBody UserEntity user);
}
