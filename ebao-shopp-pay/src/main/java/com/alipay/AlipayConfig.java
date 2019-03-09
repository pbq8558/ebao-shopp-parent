package com.alipay;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *该类在实际开发中需要将该类中的配置信息写得配置文件中去
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
	public static String app_id = "2016092200572889";
	
	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCuUXk6/naHOWwQqdb5kFHLAQMELf28xbHRqCWiMG3/BU0a5RiN7Yaea8UD2r6OQSZcu2pzegI6Q4Bbm68b8Hb42XgaqEENF099BcJ9heOAknUS8aWsK2dc/uVqGr7W7klKGk7zyUlWJLsOxkPgOgj2YYB8BiH7+FaRMgwqqnclGjC8XshAFtpkeYrr7FJF/50+w+Hkua3P2RCXdgwjWwde0l7bxnAy7i7XBMIZaP1KV2h4PNI0xeJ/IDHU6hBiL+t+wt0tde7LsWx8zUPlAkFpdHDZ3UJHhgMIZbTxVRfVsVCNrHMOaYCLV8rVnZ80nzXxhJqSRi5gT2j3VaWyWi5rAgMBAAECggEAdiiFu0ZXA4wZwyXMsTdxKeCSsdeW4clDojCO6eRX+wAV5sAZp8K0eFfmoJk6h2SD42GqY4H1VpcAv5fE13RkwINwVOltxrjhSYaI8dq6fiFIOEISIaT+GFwzF3vvlfLJPPCeScNL8ZdOMFKWGbxc7NzkYa0qhggsbZj2BEmuHzgD5L2TIjcdYDuZ7YBRhbZEytOzp4dVlIhJPCbtAX257x4P8I5V5AM6DNavfYk/2RcI2adaA5AtvuUU4v4H3LkTRp+DaUmMCN01fmCTrYhLY/waFhBpQoVk/WZ3aACsdVm8eNc+sAap/7TeU1EpMvJiT7tyeAbHyQMlCyBwiZcrgQKBgQDvb1738E2+vxLUOUxeBT+OTedtJ+5qv8pQyUjILyRB/Gt1e53w8+T+XOsQQ/tIS1d4KUPD/aADEp5nNo0H3RWJ892b5T7efoy+ZBHLPqB+UDQciMZcw+W07puUuJhUnki4Qyh6yi3Le04iY28C9xLg1wuIM8CVck+5UN/+Q5baqwKBgQC6YNIO1sto4fTnUVeHAICPpuqDsMjP6V9yYUDqZd/zyYVuVLetMN1SnYOhRSuciXIKx1svol/4qJ0hoWIylAANBwEFF0uK8UWJkZg+O2PoA245Rt6BgGI8q2UlUakx/Qw65Wi+TYyz+KomuBCDnBPUyeYtZNkT/rv6mXHHEfX7QQKBgQCKry+K3R2rBnGZiza4nsGZY3ZQu3hVTJGbCXumt+eAcPytJ9Tr49kXnCprWXULAti/OOePdjBgl0A4+Ffn3h7DsQUDb11i8loDTWMhHj1H7JzCilabFLzQeASVt/ZDzQrB7+RaexDbc6hmMsHyLbo5kVWUpqU96uBSro2PcQbH0QKBgQCLboWXrl+sjmaOa08k415KmeAWNzp0OK48Pr+23BIoKtoUyXJEMitRdB+Wlc3jpPvZTn1MbJiJyXMMRtTU694B4PyFV4EfaBLSsUaJQBsk5vQeC4PItEhKBRt9SNNgX2Q354pt3ExoE58PqmJIY4p+hbEawriX+dmdyA/Tkv9IQQKBgQDu/kIlAAs8suwbNcHFxdIQbKWIIv6FzB9qLJ6rZ3pfAyCN2xJwVo/yK0UQZQZYc4ziLGDrSDeivxsj+yDPzxJex8OBdOGmbQydFRU2q+MKPWipjAVJTACpzMF/sOqXVDOcOMRqjv6HiGvHiw7ypvljcHLUGvDwcOyx9Moh9Wk/RQ==";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3+I2auP+MzOFFIv/UbbkujefgfZIveqwIf8oykr9ON2XQaEkMX7KXZaRnniBc1GHqd+3WaA36eLS8u9vrglO1v2UOUeygjXcpZ6WkQDo+Lr46A0cUwFDMxWfzX32wk67kKHIfUulsLc9rPVBiRGzPZfM9OW7RfNnZ5tFwJ173fBSTUHiQajob/0IX0YMvO9i1nggWVh3z2VoQ4oSWtJnzBNCftykzbAvKaNZVHebu5/K//iBTxPmmY+7YhMqkFUVM1C1YTlS3Aeorp1DLXvvm9pbtW0JOMoiX/Hwx6L8EKK+gIOJdVltQWKrnk7gAY8NZKjlVQu8KPcnb5URLwJDzQIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://ebaotech.natapp1.cc/alibaba/callBack/notify_url";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://ebaotech.natapp1.cc/alibaba/callBack/return_url";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 字符编码格式
	public static String charset = "utf-8";
	
	// 支付宝网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// 支付宝网关
	public static String log_path = "E:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

