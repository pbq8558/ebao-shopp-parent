package com.ebao.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

import com.ebao.api.service.CallBackService;

@Component
@FeignClient("pay")
public interface CallBackServiceFeign extends CallBackService {

}
