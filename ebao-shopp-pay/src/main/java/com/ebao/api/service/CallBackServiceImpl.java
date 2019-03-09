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
		// 1、日志记录
		log.info("#####支付宝同步通知synCallBack#####start,params:{}", params);
		// 2、验签操作
		try {
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key,
					AlipayConfig.charset, AlipayConfig.sign_type); // 调用SDK验证签名

			// ——请在这里编写您的程序（以下代码仅作参考）——
			if (!signVerified) {
				return setResultError("验签失败！");
			}
			// 商户订单号
			String outTradeNo = params.get("out_trade_no");

			// 支付宝交易号
			String tradeNo = params.get("trade_no");

			// 付款金额
			String totalAmount = params.get("total_amount");

			JSONObject data = new JSONObject();
			data.put("outTradeNo", outTradeNo);
			data.put("tradeNo", tradeNo);
			data.put("totalAmount", totalAmount);
			return setResultSuccess(data);
		} catch (Exception e) {
			log.error("#####支付宝同步通知出现异常,error:", e);
			return setResultError("系统错误!");
		} finally {
			log.info("#####支付宝同步通知synCallBack#####end,params:{}", params);
		}
	}

	@Override
	@RequestMapping("/asynCallBack")
	public String asynCallBack(@RequestParam Map<String, String> params) {
		// 1、日志记录
		log.info("#####支付宝异步通知synCallBack#####start,params:{}", params);
		// 2、验签操作
		try {
			boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key,
					AlipayConfig.charset, AlipayConfig.sign_type); // 调用SDK验证签名

			// ——请在这里编写您的程序（以下代码仅作参考）——
			if (!signVerified) {
				return Constants.PAY_FAIL;
			}
			// 商户订单号
			String outTradeNo = params.get("out_trade_no");
			PaymentInfo paymentInfo = paymentInfoDao.getByOrderIdPayInfo(outTradeNo);
			if (paymentInfo == null) {
				return Constants.PAY_FAIL;
			}

			// 用支付状态来解决重试机制中的幂等性问题
			if (paymentInfo.getState() == 1) {
				// 如果此时支付信息状态是已支付，直接返回成功
				return Constants.PAY_SUCCESS;
			}

			// 支付宝交易号
			String tradeNo = params.get("trade_no");

			// 付款金额
			// String totalAmount = params.get("total_amount");
			// 另外还需要判断实际付款金额是否与支付信息金额一致，如果不一致，就把支付信息和订单状态改为异常，该处判断省略

			// 修改支付表状态信息
			paymentInfo.setState(1);// 表示已支付状态
			paymentInfo.setPayMessage(params.toString());
			paymentInfo.setPlatformorderId(tradeNo);
			Integer updateResult = paymentInfoDao.updatePayInfo(paymentInfo);
			if (updateResult <= 0) {
				return Constants.PAY_FAIL;
			}
			// 调用订单接口通知支付状态更改
			ResponseBase orderResult = orderServiceFeign.updateOrder(1L, tradeNo, outTradeNo);
			if (!Constants.HTTP_RES_CODE_200.equals(orderResult.getRtnCode())) {
				
			}
			log.info("#####asynCallBack###finished!!!!");
			return Constants.PAY_SUCCESS;
		} catch (Exception e) {
			log.error("#####支付宝异步通知出现异常,error:", e);
			return Constants.PAY_FAIL;
		} finally {
			log.info("#####支付宝异步通知synCallBack#####end,params:{}", params);
		}
	}

}
