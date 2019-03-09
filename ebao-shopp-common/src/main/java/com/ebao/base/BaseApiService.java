package com.ebao.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ebao.constants.Constants;

@Component
public class BaseApiService {

	@Autowired
	protected BaseRedisService baseRedisService;

	// 返回失败,可以指定错误信息
	public ResponseBase setResultError(String msg) {
		return setResult(Constants.HTTP_RES_CODE_500, msg, null);

	}

	// 返回失败,可以指定错误信息
	public ResponseBase setResultError(Integer code, String msg) {
		return setResult(code, msg, null);

	}

	// 返回成功且有data值
	public ResponseBase setResultSuccess(Object data) {
		return setResult(Constants.HTTP_RES_CODE_200, Constants.HTTP_RES_CODE_200_VALUE, data);

	}

	// 返回成功且无data值
	public ResponseBase setResultSuccess(String msg) {
		return setResult(Constants.HTTP_RES_CODE_200, msg, null);

	}

	// 返回成功但无data值
	public ResponseBase setResultSuccess() {
		return setResult(Constants.HTTP_RES_CODE_200, Constants.HTTP_RES_CODE_200_VALUE, null);

	}

	// 通用封装
	public ResponseBase setResult(Integer rtnCode, String msg, Object data) {
		return new ResponseBase(rtnCode, msg, data);
	};

}
