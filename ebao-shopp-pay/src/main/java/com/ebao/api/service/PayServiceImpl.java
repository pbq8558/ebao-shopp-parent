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
	 * ����֧������
	 * 
	 * @param paymentInfo
	 * @return
	 */
	@RequestMapping("/createToken")
	public ResponseBase createToken(@RequestBody PaymentInfo paymentInfo) {
		// 1������֧��������Ϣ
		Integer savePaymentType = paymentInfoDao.savePaymentType(paymentInfo);
		if (savePaymentType <= 0) {
			return setResultError("����֧��������Ϣʧ�ܣ�");
		}
		// 2�����ɶ�Ӧ��token
		String payToken = TokenUtils.getPayToken();
		// 3�������redis�У���tokenΪkeyֵ����֧��idΪvalueֵ��������ʧЧʱ��Ϊ15����
		baseRedisService.setString(payToken, paymentInfo.getId() + "", Constants.PAY_TOKEN_TIMEOUT);
		// 4������token
		JSONObject data = new JSONObject();
		data.put("payToken", payToken);
		return setResultSuccess(data);
	}

	/**
	 * ʹ��֧�����Ʋ���֧����Ϣ
	 * 
	 * @param paymentInfo
	 * @return
	 * @throws AlipayApiException 
	 */
	@RequestMapping("/findPayToken")
	public ResponseBase findPayToken(@RequestParam("token") String token) {
		//1��������֤
		if (StringUtils.isEmpty(token)) {
			return setResultError("token����Ϊ��");
		}
		//2~3���ж�token����Ч��;ʹ��token����redis���ҵ���Ӧ��֧��id
		String payId = (String) baseRedisService.getString(token);
		if (StringUtils.isEmpty(payId)){
			return setResultError("֧�������ѳ�ʱ��");
		}
		//4��ʹ��֧��id��ѯ������Ϣ
		Long id = Long.parseLong(payId);
		PaymentInfo paymentInfo = paymentInfoDao.getPaymentInfo(id);
		if (paymentInfo == null) {
			setResultError("Ϊ�ҵ�֧����Ϣ");
		}
		//5���Խ�֧��������,�����ύ֧����form�����ͻ���
		//��ó�ʼ����AlipayClient
		AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
		
		//�����������
		AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
		alipayRequest.setReturnUrl(AlipayConfig.return_url);
		alipayRequest.setNotifyUrl(AlipayConfig.notify_url);
		
		//�̻������ţ��̻���վ����ϵͳ��Ψһ�����ţ�����
		String out_trade_no = paymentInfo.getOrderId();
		//���������(֧��������ԪΪ��λ��)
		String total_amount = paymentInfo.getPrice() + "";
		//�������ƣ�����(һ��������������Ȼ��ӱ��ж�ȡ)
		String subject = "eBao�Ƽ���Ա��ֵ";
		//��Ʒ�������ɿ�
		//String body = new String(request.getParameter("WIDbody").getBytes("ISO-8859-1"),"UTF-8");
		
		alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
				+ "\"total_amount\":\""+ total_amount +"\"," 
				+ "\"subject\":\""+ subject +"\"," 
//				+ "\"body\":\""+ body +"\"," 
				+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		
		//�����BizContent����������ѡ����������������Զ��峬ʱʱ�����timeout_express������˵��
		//alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," 
		//		+ "\"total_amount\":\""+ total_amount +"\"," 
		//		+ "\"subject\":\""+ subject +"\"," 
		//		+ "\"body\":\""+ body +"\"," 
		//		+ "\"timeout_express\":\"10m\"," 
		//		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
		//��������ɲ��ġ�������վ֧����API�ĵ�-alipay.trade.page.pay-����������½�
		
		//����
		String result = "";
		try {
			result = alipayClient.pageExecute(alipayRequest).getBody();
		} catch (AlipayApiException e) {
			return setResultError("֧���ӿ��쳣");
		}
		
		JSONObject data = new JSONObject();
		data.put("payHtml", result);
		//���
//		out.println(result);
		return setResultSuccess(data);
	}
}
