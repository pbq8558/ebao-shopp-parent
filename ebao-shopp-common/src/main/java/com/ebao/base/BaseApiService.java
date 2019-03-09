package com.ebao.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebao.constants.Constants;

@Component
public class BaseApiService {

	@Autowired
	protected BaseRedisService baseRedisService;

	// ����ʧ��,����ָ��������Ϣ
	public ResponseBase setResultError(String msg) {
		return setResult(Constants.HTTP_RES_CODE_500, msg, null);

	}

	// ����ʧ��,����ָ��������Ϣ
	public ResponseBase setResultError(Integer code, String msg) {
		return setResult(code, msg, null);

	}

	// ���سɹ�����dataֵ
	public ResponseBase setResultSuccess(Object data) {
		return setResult(Constants.HTTP_RES_CODE_200, Constants.HTTP_RES_CODE_200_VALUE, data);

	}

	// ���سɹ�����dataֵ
	public ResponseBase setResultSuccess(String msg) {
		return setResult(Constants.HTTP_RES_CODE_200, msg, null);

	}

	// ���سɹ�����dataֵ
	public ResponseBase setResultSuccess() {
		return setResult(Constants.HTTP_RES_CODE_200, Constants.HTTP_RES_CODE_200_VALUE, null);

	}

	// ͨ�÷�װ
	public ResponseBase setResult(Integer rtnCode, String msg, Object data) {
		return new ResponseBase(rtnCode, msg, data);
	};

}
