package com.ebao.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codingapi.tx.annotation.ITxTransaction;
import com.ebao.api.order.OrderService;
import com.ebao.base.BaseApiService;
import com.ebao.base.ResponseBase;
import com.ebao.dao.OrderDao;

@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService, ITxTransaction {

	@Autowired
	private OrderDao orderDao;

	@Override
	@RequestMapping("/updateOrder")
	@Transactional
	public ResponseBase updateOrder(@RequestParam("payStatus") Long payStatus, @RequestParam("payId") String aliPayId,
			@RequestParam("orderNumber") String orderNumber) {
		int updateOrder = orderDao.updateOrder(payStatus, aliPayId, orderNumber);
		if (updateOrder <= 0) {
			return setResultError("ÏµÍ³´íÎó!");
		}
		return setResultSuccess();
	}

}
