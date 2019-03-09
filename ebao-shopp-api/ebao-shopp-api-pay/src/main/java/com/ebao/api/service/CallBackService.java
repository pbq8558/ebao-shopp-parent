package com.ebao.api.service;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebao.base.ResponseBase;

@RequestMapping("/callBackService")
public interface CallBackService {

	// ͬ��֪ͨ
	@RequestMapping("/synCallBack")
	public ResponseBase synCallBack(@RequestParam Map<String, String> params);

	// �첽֪ͨ
	@RequestMapping("/asynCallBack")
	public String asynCallBack(@RequestParam Map<String, String> params);
}
