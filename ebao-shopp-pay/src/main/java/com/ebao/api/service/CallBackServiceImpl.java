package com.ebao.api.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alipay.AlipayConfig;
import com.alipay.api.internal.util.AlipaySignature;
import com.ebao.base.BaseApiService;
import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.dao.PaymentInfoDao;
import com.ebao.entity.PaymentInfo;
import com.ebao.feign.OrderServiceFeign;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CallBackServiceImpl extends BaseApiService implements CallBackService {
	@Autowired
	private PaymentInfoDao paymentInfoDao;
	@Autowired
	private OrderServiceFeign orderServiceFeign;

	@Override
	@RequestMapping("/synCallBack")
	public ResponseBase synCallBack(@RequestParam Map<String, String> params) {
		// 1����־��¼
		log.info("#####֧����ͬ��֪ͨsynCallBack#####start,params:{}", params);
		// 2����ǩ����
		try {
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key,
					AlipayConfig.charset, AlipayConfig.sign_type); // ����SDK��֤ǩ��

			// �������������д���ĳ������´�������ο�������
			if (!signVerified) {
				return setResultError("��ǩʧ�ܣ�");
			}
			// �̻�������
			String outTradeNo = params.get("out_trade_no");

			// ֧�������׺�
			String tradeNo = params.get("trade_no");

			// ������
			String totalAmount = params.get("total_amount");

			JSONObject data = new JSONObject();
			data.put("outTradeNo", outTradeNo);
			data.put("tradeNo", tradeNo);
			data.put("totalAmount", totalAmount);
			return setResultSuccess(data);
		} catch (Exception e) {
			log.error("#####֧����ͬ��֪ͨ�����쳣,error:", e);
			return setResultError("ϵͳ����!");
		} finally {
			log.info("#####֧����ͬ��֪ͨsynCallBack#####end,params:{}", params);
		}
	}

	@Override
	@RequestMapping("/asynCallBack")
	public String asynCallBack(@RequestParam Map<String, String> params) {
		// 1����־��¼
		log.info("#####֧�����첽֪ͨsynCallBack#####start,params:{}", params);
		// 2����ǩ����
		try {
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key,
					AlipayConfig.charset, AlipayConfig.sign_type); // ����SDK��֤ǩ��

			// �������������д���ĳ������´�������ο�������
			if (!signVerified) {
				return Constants.PAY_FAIL;
			}
			// �̻�������
			String outTradeNo = params.get("out_trade_no");
			PaymentInfo paymentInfo = paymentInfoDao.getByOrderIdPayInfo(outTradeNo);
			if (paymentInfo == null) {
				return Constants.PAY_FAIL;
			}

			// ��֧��״̬��������Ի����е��ݵ�������
			if (paymentInfo.getState() == 1) {
				// �����ʱ֧����Ϣ״̬����֧����ֱ�ӷ��سɹ�
				return Constants.PAY_SUCCESS;
			}

			// ֧�������׺�
			String tradeNo = params.get("trade_no");

			// ������
			// String totalAmount = params.get("total_amount");
			// ���⻹��Ҫ�ж�ʵ�ʸ������Ƿ���֧����Ϣ���һ�£������һ�£��Ͱ�֧����Ϣ�Ͷ���״̬��Ϊ�쳣���ô��ж�ʡ��

			// �޸�֧����״̬��Ϣ
			paymentInfo.setState(1);// ��ʾ��֧��״̬
			paymentInfo.setPayMessage(params.toString());
			paymentInfo.setPlatformorderId(tradeNo);
			Integer updateResult = paymentInfoDao.updatePayInfo(paymentInfo);
			if (updateResult <= 0) {
				return Constants.PAY_FAIL;
			}
			// ���ö����ӿ�֪֧ͨ��״̬����
			ResponseBase orderResult = orderServiceFeign.updateOrder(1L, tradeNo, outTradeNo);
			if (!Constants.HTTP_RES_CODE_200.equals(orderResult.getRtnCode())) {
				
			}
			log.info("#####asynCallBack###finished!!!!");
			return Constants.PAY_SUCCESS;
		} catch (Exception e) {
			log.error("#####֧�����첽֪ͨ�����쳣,error:", e);
			return Constants.PAY_FAIL;
		} finally {
			log.info("#####֧�����첽֪ͨsynCallBack#####end,params:{}", params);
		}
	}

}
