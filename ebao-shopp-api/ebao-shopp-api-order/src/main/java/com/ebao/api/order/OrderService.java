package com.ebao.api.order;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ebao.base.ResponseBase;

@RequestMapping("/order")
public interface OrderService {

	@RequestMapping("/updateOrder")
	public ResponseBase updateOrder(@RequestParam("payStatus") Long payStatus, @RequestParam("payId") String payId,
			@RequestParam("orderNumber") String orderNumber);
}
