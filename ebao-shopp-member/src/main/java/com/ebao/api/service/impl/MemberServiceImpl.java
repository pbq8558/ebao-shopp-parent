package com.ebao.api.service.impl;

import java.util.Date;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.ebao.api.service.MemberService;
import com.ebao.base.BaseApiService;
import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.dao.MemberDao;
import com.ebao.entity.UserEntity;
import com.ebao.mq.RegisterMailboxProducer;
import com.ebao.utils.MD5Util;
import com.ebao.utils.TokenUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MemberServiceImpl extends BaseApiService implements MemberService {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private RegisterMailboxProducer registerMailboxProducer;
	@Value("${messages.queue}")
	private String MESSAGESQUEUE;

	@Override
	public ResponseBase findUserById(Long id) {
		UserEntity userEntity = memberDao.findById(id);
		if (userEntity == null) {
			return setResultError("未查到到任何数据！");
		}
		return setResultSuccess(userEntity);
	}

	@Override
	public ResponseBase regUser(@RequestBody UserEntity user) {
		// 参数验证省略。。。
		// 对密码加密
		String password = user.getPassword();
		if (StringUtils.isEmpty(password)) {
			return setResultError("密码不能为空！");
		}
		String newPassword = MD5Util.MD5(password);
		user.setPassword(newPassword);
		user.setCreated(new Date());
		user.setUpdated(new Date());
		Integer result = memberDao.insertUser(user);
		if (result <= 0) {
			return setResultError("注册用户信息失败！");
		}
		String json = emailJson(user.getEmail());
		log.info("#######会员服务推送消息到消息服务平台#######json:{}", json);
		sendMessage(json);
		return setResultSuccess("用户注册成功！");
	}

	private String emailJson(String email) {
		JSONObject rootJson = new JSONObject();
		JSONObject header = new JSONObject();
		header.put("interfaceType", Constants.MSG_EMAIL);
		JSONObject content = new JSONObject();
		content.put("email", email);
		rootJson.put("header", header);
		rootJson.put("content", content);
		return rootJson.toJSONString();
	}

	private void sendMessage(String json) {
		ActiveMQQueue activeMQQueue = new ActiveMQQueue(MESSAGESQUEUE);
		registerMailboxProducer.sendMsg(activeMQQueue, json);
	}

	@Override
	public ResponseBase login(@RequestBody UserEntity user) {
		// 1、验证参数
		String username = user.getUsername();
		if (StringUtils.isEmpty(username)) {
			return setResultError("用户名称不能为空！");
		}
		String password = user.getPassword();
		if (StringUtils.isEmpty(password)) {
			return setResultError("密码不能为空！");
		}
		// 2、数据库查找账号密码是否正确
		String md5Password = MD5Util.MD5(password);
		UserEntity userEntity = memberDao.login(username, md5Password);
		return setLogin(userEntity);
	}

	private ResponseBase setLogin(UserEntity userEntity) {
		if (userEntity == null) {
			return setResultError("账号或密码不存在！");
		}
		// 3、如果账号密码正确，生成对应的token
		String memberToken = TokenUtils.getMemberToken();
		// 4、存放在redis中，key为token，value为userid
		// 最好不要直接存用户的json格式信息（以防止数据库用户信息更改导致数据不一致）
		log.info("####USER INFORMATION INTO REDIS... KEY:{},VALUE:{}", memberToken, userEntity.getId());
		baseRedisService.setString(memberToken, userEntity.getId() + "", Constants.TOKEN_MEMBER_TIMEOUT);
		// 5、直接返回Token
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("memberToken", memberToken);
		return setResultSuccess(jsonObject);
	}

	@Override
	public ResponseBase findByTokenUser(@RequestParam("token") String token) {
		// 1、验证参数
		if (StringUtils.isEmpty(token)) {
			return setResultError("token不能为空！");
		}
		// 2、使用token在redis中查找userId
		String strUserId = (String) baseRedisService.getString(token);
		if (StringUtils.isEmpty(strUserId)) {
			return setResultError("token无效或已过期！");
		}
		// 3、使用userId在数据库中查找用户信息，返回给客户端
		Long userId = Long.parseLong(strUserId);
		UserEntity userEntity = memberDao.findById(userId);
		if (userEntity == null) {
			return setResultError("未查找到该用户信息！");
		}
		userEntity.setPassword(null);
		return setResultSuccess(userEntity);
	}

	@Override
	public ResponseBase findByOpenIdUser(@RequestParam("openid") String openid) {
		// 1.验证参数
		if (StringUtils.isEmpty(openid)) {
			return setResultError("openid不能为空！");
		}
		// 2.使用openid 查询数据库user表对应数据信息
		UserEntity userEntity = memberDao.findByOpenIdUser(openid);
		if (userEntity == null) {
			return setResultError(Constants.HTTP_RES_CODE_201, "该openid没有关联！");
		}
		// 3.自动登录
		return setLogin(userEntity);
	}

	@Override
	public ResponseBase qqLogin(@RequestBody UserEntity user) {
		// 1.验证参数
		String openid = user.getOpenid();
		if (StringUtils.isEmpty(openid)) {
			return setResultError("openid不能为空！");
		}
		// 2.先进行账号登录
		ResponseBase setLogin = login(user);
		if (!Constants.HTTP_RES_CODE_200.equals(setLogin.getRtnCode())) {
			// 3.如果登录不成功，返回错误信息给客户端
			return setLogin;
		}
		//此处注意，不是通过rpc远程调用直接调用本地方法的就直接是实际的类型JSONObject
		//而通过rpc调用的返回类型是LinkedHashMap类型
		JSONObject jsonObject = (JSONObject) setLogin.getData();
		String token = jsonObject.getString("memberToken");
		ResponseBase newUserBase = findByTokenUser(token);
		if (!Constants.HTTP_RES_CODE_200.equals(newUserBase.getRtnCode())) {
			return newUserBase;
		}
		UserEntity newUser = (UserEntity) newUserBase.getData();
		Integer id = newUser.getId();
		if (!Constants.HTTP_RES_CODE_200.equals(setLogin.getRtnCode())) {
			// 4.如果登录成功，修改数据库对应的openid
			Integer num = memberDao.updateByOpenIdUser(openid,id);
			if (num <= 0) {
				return setResultError("QQ账号关联失败！");
			}
		}
		return setLogin;
	}

}
