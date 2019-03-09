package com.ebao.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codingapi.tx.annotation.TxTransaction;
import com.ebao.api.service.PayOrderService;
import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.dao.PaymentInfoDao;
import com.ebao.entity.PaymentInfo;
import com.ebao.feign.OrderServiceFeign;

@RestController
public class PayOrderServiceImpl implements PayOrderService {
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	@Autowired
	private OrderServiceFeign orderServiceFegin;

	@SuppressWarnings("unused")
	@TxTransaction(isStart = true)//��ʾ����ķ����
	@Transactional
	public String payOrder(@RequestParam("orderId") String orderId, @RequestParam("temp") int temp) {
		PaymentInfo paymentInfo = paymentInfoDao.getByOrderIdPayInfo(orderId);
		if (paymentInfo == null) {
			return Constants.PAY_FAIL;
		}

		// ֧�������Ի���
		Integer state = paymentInfo.getState();
		if (state == 1) {
			return Constants.PAY_SUCCESS;
		}

		// ֧�������׺�
		String tradeNo = "2019010622001446050500350454";
		paymentInfo.setState(1);// ��ʶΪ�Ѿ�֧��
		paymentInfo.setPayMessage("test");
		paymentInfo.setPlatformorderId(tradeNo);
		// �ֶ� begin begin
		Integer updateResult = paymentInfoDao.updatePayInfo(paymentInfo);
		if (updateResult <= 0) {
			return Constants.PAY_FAIL;
		}

		// ���ö����ӿ�֪ͨ ֧��״̬
		ResponseBase orderResult = orderServiceFegin.updateOrder(1l, tradeNo, orderId);
		if (!orderResult.getRtnCode().equals(Constants.HTTP_RES_CODE_200)) {
			// �ع� �ֶ��ع� rollback

			return Constants.PAY_FAIL;
		} // 2PC 3PC TCC MQ
		int i = 1 / temp;
		return Constants.PAY_SUCCESS;
	}

}
