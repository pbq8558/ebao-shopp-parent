package com.ebao.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;

import com.ebao.api.service.MemberService;

@Component
@FeignClient("member")
public interface MemberServiceFeign extends MemberService {

}
