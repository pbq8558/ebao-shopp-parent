package com.ebao.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

import com.ebao.api.service.PayService;

@Component
@FeignClient("pay")
public interface PayServiceFeign extends PayService {

}
