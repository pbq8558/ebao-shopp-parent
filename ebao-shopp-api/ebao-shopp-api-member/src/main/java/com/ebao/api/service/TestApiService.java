package com.ebao.api.service;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.ebao.base.ResponseBase;

@RequestMapping("/member")
public interface TestApiService {

	@RequestMapping("/test")
	public Map<String, Object> test(Integer id, String name);

	@RequestMapping("/testResponseBase")
	public ResponseBase testResponseBase();
	
	@RequestMapping("/setTestRedis")
	public ResponseBase setTestRedis(String key, String value);
}
