package com.ebao.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

import com.ebao.api.order.OrderService;

@Component
@FeignClient("order")
public interface OrderServiceFeign extends OrderService {

}
