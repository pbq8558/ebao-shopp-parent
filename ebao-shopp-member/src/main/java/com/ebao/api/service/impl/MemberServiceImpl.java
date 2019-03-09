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
			return setResultError("δ�鵽���κ����ݣ�");
		}
		return setResultSuccess(userEntity);
	}

	@Override
	public ResponseBase regUser(@RequestBody UserEntity user) {
		// ������֤ʡ�ԡ�����
		// ���������
		String password = user.getPassword();
		if (StringUtils.isEmpty(password)) {
			return setResultError("���벻��Ϊ�գ�");
		}
		String newPassword = MD5Util.MD5(password);
		user.setPassword(newPassword);
		user.setCreated(new Date());
		user.setUpdated(new Date());
		Integer result = memberDao.insertUser(user);
		if (result <= 0) {
			return setResultError("ע���û���Ϣʧ�ܣ�");
		}
		String json = emailJson(user.getEmail());
		log.info("#######��Ա����������Ϣ����Ϣ����ƽ̨#######json:{}", json);
		sendMessage(json);
		return setResultSuccess("�û�ע��ɹ���");
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
		// 1����֤����
		String username = user.getUsername();
		if (StringUtils.isEmpty(username)) {
			return setResultError("�û����Ʋ���Ϊ�գ�");
		}
		String password = user.getPassword();
		if (StringUtils.isEmpty(password)) {
			return setResultError("���벻��Ϊ�գ�");
		}
		// 2�����ݿ�����˺������Ƿ���ȷ
		String md5Password = MD5Util.MD5(password);
		UserEntity userEntity = memberDao.login(username, md5Password);
		return setLogin(userEntity);
	}

	private ResponseBase setLogin(UserEntity userEntity) {
		if (userEntity == null) {
			return setResultError("�˺Ż����벻���ڣ�");
		}
		// 3������˺�������ȷ�����ɶ�Ӧ��token
		String memberToken = TokenUtils.getMemberToken();
		// 4�������redis�У�keyΪtoken��valueΪuserid
		// ��ò�Ҫֱ�Ӵ��û���json��ʽ��Ϣ���Է�ֹ���ݿ��û���Ϣ���ĵ������ݲ�һ�£�
		log.info("####USER INFORMATION INTO REDIS... KEY:{},VALUE:{}", memberToken, userEntity.getId());
		baseRedisService.setString(memberToken, userEntity.getId() + "", Constants.TOKEN_MEMBER_TIMEOUT);
		// 5��ֱ�ӷ���Token
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("memberToken", memberToken);
		return setResultSuccess(jsonObject);
	}

	@Override
	public ResponseBase findByTokenUser(@RequestParam("token") String token) {
		// 1����֤����
		if (StringUtils.isEmpty(token)) {
			return setResultError("token����Ϊ�գ�");
		}
		// 2��ʹ��token��redis�в���userId
		String strUserId = (String) baseRedisService.getString(token);
		if (StringUtils.isEmpty(strUserId)) {
			return setResultError("token��Ч���ѹ��ڣ�");
		}
		// 3��ʹ��userId�����ݿ��в����û���Ϣ�����ظ��ͻ���
		Long userId = Long.parseLong(strUserId);
		UserEntity userEntity = memberDao.findById(userId);
		if (userEntity == null) {
			return setResultError("δ���ҵ����û���Ϣ��");
		}
		userEntity.setPassword(null);
		return setResultSuccess(userEntity);
	}

	@Override
	public ResponseBase findByOpenIdUser(@RequestParam("openid") String openid) {
		// 1.��֤����
		if (StringUtils.isEmpty(openid)) {
			return setResultError("openid����Ϊ�գ�");
		}
		// 2.ʹ��openid ��ѯ���ݿ�user���Ӧ������Ϣ
		UserEntity userEntity = memberDao.findByOpenIdUser(openid);
		if (userEntity == null) {
			return setResultError(Constants.HTTP_RES_CODE_201, "��openidû�й�����");
		}
		// 3.�Զ���¼
		return setLogin(userEntity);
	}

	@Override
	public ResponseBase qqLogin(@RequestBody UserEntity user) {
		// 1.��֤����
		String openid = user.getOpenid();
		if (StringUtils.isEmpty(openid)) {
			return setResultError("openid����Ϊ�գ�");
		}
		// 2.�Ƚ����˺ŵ�¼
		ResponseBase setLogin = login(user);
		if (!Constants.HTTP_RES_CODE_200.equals(setLogin.getRtnCode())) {
			// 3.�����¼���ɹ������ش�����Ϣ���ͻ���
			return setLogin;
		}
		//�˴�ע�⣬����ͨ��rpcԶ�̵���ֱ�ӵ��ñ��ط����ľ�ֱ����ʵ�ʵ�����JSONObject
		//��ͨ��rpc���õķ���������LinkedHashMap����
		JSONObject jsonObject = (JSONObject) setLogin.getData();
		String token = jsonObject.getString("memberToken");
		ResponseBase newUserBase = findByTokenUser(token);
		if (!Constants.HTTP_RES_CODE_200.equals(newUserBase.getRtnCode())) {
			return newUserBase;
		}
		UserEntity newUser = (UserEntity) newUserBase.getData();
		Integer id = newUser.getId();
		if (!Constants.HTTP_RES_CODE_200.equals(setLogin.getRtnCode())) {
			// 4.�����¼�ɹ����޸����ݿ��Ӧ��openid
			Integer num = memberDao.updateByOpenIdUser(openid,id);
			if (num <= 0) {
				return setResultError("QQ�˺Ź���ʧ�ܣ�");
			}
		}
		return setLogin;
	}

}
