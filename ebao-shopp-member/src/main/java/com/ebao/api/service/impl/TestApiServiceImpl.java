package com.ebao.api.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RestController;

import com.ebao.api.service.TestApiService;
import com.ebao.base.BaseApiService;
import com.ebao.base.ResponseBase;

@RestController
public class TestApiServiceImpl extends BaseApiService implements TestApiService {

	@Override
	public Map<String, Object> test(Integer id, String name) {
		Map<String, Object> result = new HashMap<>();
		result.put("rtnCode", "200");
		result.put("rtnMsg", "success");
		result.put("data", "id: " + id + ", name:" + name);
		return result;
	}

	@Override
	public ResponseBase testResponseBase() {
		return setResultSuccess();
	}

	@Override
	public ResponseBase setTestRedis(String key, String value) {
		baseRedisService.setString(key, value, null);
		return setResultSuccess();
	}

}
