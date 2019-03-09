package com.ebao.api.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alipay.AlipayConfig;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.ebao.base.BaseApiService;
import com.ebao.base.ResponseBase;
import com.ebao.constants.Constants;
import com.ebao.dao.PaymentInfoDao;
import com.ebao.entity.PaymentInfo;
import com.ebao.utils.TokenUtils;

@RestController
public class PayServiceImpl extends BaseApiService implements PayService {

	@Autowired
	private PaymentInfoDao paymentInfoDao;

	/**
	 * 创建支付令牌
	 * 
	 * @param paymentInfo
	 * @return
	 */
	@RequestMapping("/createToken")
	public ResponseBase createToken(@RequestBody PaymentInfo paymentInfo) {
		// 1、创建支付请求信息
		Integer savePaymentType = paymentInfoDao.savePaymentType(paymentInfo);
		if (savePaymentType <= 0) {
			return setResultError("创建支付订单信息失败！");
		}
		// 2、生成对应的token
		String payToken = TokenUtils.getPayToken();
		// 3、存放在redis中，以token为key值，以支付id为value值，并设置失效时间为15分钟
		baseRedisService.setString(payToken, paymentInfo.getId() + "", Constants.PAY_TOKEN_TIMEOUT);
		// 4、返回token
		JSONObject data = new JSONObject();
		data.put("payToken", payToken);
		return setResultSuccess(data);
	}

	/**
	 * 使用支付令牌查找支付信息
	 * 
	 * @param paymentInfo
	 * @return
	 * @throws AlipayApiException 
	 */
	@RequestMapping("/findPayToken")
	public ResponseBase findPayToken(@RequestParam("token") String token) {
		//1、参数验证
		if (StringUtils.isEmpty(token)) {
			return setResultError("token不能为空");
		}
		//2~3、判断token的有效期;使用token查找redis，找到对应的支付id
		String payId = (String) baseRedisService.getString(token);
		if (StringUtils.isEmpty(payId)){
			return setResultError("支付请求已超时！");
		}
		//4、使用支付id查询订单信息
		Long id = Long.parseLong(payId);
		PaymentInfo paymentInfo = paymentInfoDao.getPaymentInfo(id);
		if (paymentInfo == null) {
			setResultError("为找到支付信息");
		}
		//5、对接支付宝代码,返回提交支付的form表单给客户端
		//获得初始化的AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
		
		//设置请求参数
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(AlipayConfig.return_url);
		alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
		
		//商户订单号，商户网站订单系统中唯一订单号，必填
		String out_trade_no = paymentInfo.getOrderId();
		//付款金额，必填(支付宝是以元为单位的)
		String total_amount = paymentInfo.getPrice() + "";
		//订单名称，必填(一般情况下这个会存表，然后从表中读取)
		String subject = "eBao科技会员充值";
		//商品描述，可空
		//String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
		
		alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
				+ "\"total_amount\":\""+ total_amount +"\"," 
				+ "\"subject\":\""+ subject +"\"," 
//				+ "\"body\":\""+ body +"\"," 
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		
		//若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
		//alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
		//		+ "\"total_amount\":\""+ total_amount +"\"," 
		//		+ "\"subject\":\""+ subject +"\"," 
		//		+ "\"body\":\""+ body +"\"," 
		//		+ "\"timeout_express\":\"10m\"," 
		//		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		//请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节
		
		//请求
		String result = "";
		try {
			result = alipayClient.pageExecute(alipayRequest).getBody();
		} catch (AlipayApiException e) {
			return setResultError("支付接口异常");
		}
		
		JSONObject data = new JSONObject();
		data.put("payHtml", result);
		//输出
//		out.println(result);
		return setResultSuccess(data);
	}
}
