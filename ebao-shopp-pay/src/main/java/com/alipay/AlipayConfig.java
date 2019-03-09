package com.alipay;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *������ʵ�ʿ�������Ҫ�������е�������Ϣд�������ļ���ȥ
 *������AlipayConfig
 *���ܣ�����������
 *��ϸ�������ʻ��й���Ϣ������·��
 *�޸����ڣ�2017-04-05
 *˵����
 *���´���ֻ��Ϊ�˷����̻����Զ��ṩ���������룬�̻����Ը����Լ���վ����Ҫ�����ռ����ĵ���д,����һ��Ҫʹ�øô��롣
 *�ô������ѧϰ���о�֧�����ӿ�ʹ�ã�ֻ���ṩһ���ο���
 */

public class AlipayConfig {
	
//�����������������������������������Ļ�����Ϣ������������������������������

	// Ӧ��ID,����APPID���տ��˺ż�������APPID��Ӧ֧�����˺�
	public static String app_id = "2016092200572889";
	
	// �̻�˽Կ������PKCS8��ʽRSA2˽Կ
    public static String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCuUXk6/naHOWwQqdb5kFHLAQMELf28xbHRqCWiMG3/BU0a5RiN7Yaea8UD2r6OQSZcu2pzegI6Q4Bbm68b8Hb42XgaqEENF099BcJ9heOAknUS8aWsK2dc/uVqGr7W7klKGk7zyUlWJLsOxkPgOgj2YYB8BiH7+FaRMgwqqnclGjC8XshAFtpkeYrr7FJF/50+w+Hkua3P2RCXdgwjWwde0l7bxnAy7i7XBMIZaP1KV2h4PNI0xeJ/IDHU6hBiL+t+wt0tde7LsWx8zUPlAkFpdHDZ3UJHhgMIZbTxVRfVsVCNrHMOaYCLV8rVnZ80nzXxhJqSRi5gT2j3VaWyWi5rAgMBAAECggEAdiiFu0ZXA4wZwyXMsTdxKeCSsdeW4clDojCO6eRX+wAV5sAZp8K0eFfmoJk6h2SD42GqY4H1VpcAv5fE13RkwINwVOltxrjhSYaI8dq6fiFIOEISIaT+GFwzF3vvlfLJPPCeScNL8ZdOMFKWGbxc7NzkYa0qhggsbZj2BEmuHzgD5L2TIjcdYDuZ7YBRhbZEytOzp4dVlIhJPCbtAX257x4P8I5V5AM6DNavfYk/2RcI2adaA5AtvuUU4v4H3LkTRp+DaUmMCN01fmCTrYhLY/waFhBpQoVk/WZ3aACsdVm8eNc+sAap/7TeU1EpMvJiT7tyeAbHyQMlCyBwiZcrgQKBgQDvb1738E2+vxLUOUxeBT+OTedtJ+5qv8pQyUjILyRB/Gt1e53w8+T+XOsQQ/tIS1d4KUPD/aADEp5nNo0H3RWJ892b5T7efoy+ZBHLPqB+UDQciMZcw+W07puUuJhUnki4Qyh6yi3Le04iY28C9xLg1wuIM8CVck+5UN/+Q5baqwKBgQC6YNIO1sto4fTnUVeHAICPpuqDsMjP6V9yYUDqZd/zyYVuVLetMN1SnYOhRSuciXIKx1svol/4qJ0hoWIylAANBwEFF0uK8UWJkZg+O2PoA245Rt6BgGI8q2UlUakx/Qw65Wi+TYyz+KomuBCDnBPUyeYtZNkT/rv6mXHHEfX7QQKBgQCKry+K3R2rBnGZiza4nsGZY3ZQu3hVTJGbCXumt+eAcPytJ9Tr49kXnCprWXULAti/OOePdjBgl0A4+Ffn3h7DsQUDb11i8loDTWMhHj1H7JzCilabFLzQeASVt/ZDzQrB7+RaexDbc6hmMsHyLbo5kVWUpqU96uBSro2PcQbH0QKBgQCLboWXrl+sjmaOa08k415KmeAWNzp0OK48Pr+23BIoKtoUyXJEMitRdB+Wlc3jpPvZTn1MbJiJyXMMRtTU694B4PyFV4EfaBLSsUaJQBsk5vQeC4PItEhKBRt9SNNgX2Q354pt3ExoE58PqmJIY4p+hbEawriX+dmdyA/Tkv9IQQKBgQDu/kIlAAs8suwbNcHFxdIQbKWIIv6FzB9qLJ6rZ3pfAyCN2xJwVo/yK0UQZQZYc4ziLGDrSDeivxsj+yDPzxJex8OBdOGmbQydFRU2q+MKPWipjAVJTACpzMF/sOqXVDOcOMRqjv6HiGvHiw7ypvljcHLUGvDwcOyx9Moh9Wk/RQ==";
	
	// ֧������Կ,�鿴��ַ��https://openhome.alipay.com/platform/keyManage.htm ��ӦAPPID�µ�֧������Կ��
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3+I2auP+MzOFFIv/UbbkujefgfZIveqwIf8oykr9ON2XQaEkMX7KXZaRnniBc1GHqd+3WaA36eLS8u9vrglO1v2UOUeygjXcpZ6WkQDo+Lr46A0cUwFDMxWfzX32wk67kKHIfUulsLc9rPVBiRGzPZfM9OW7RfNnZ5tFwJ173fBSTUHiQajob/0IX0YMvO9i1nggWVh3z2VoQ4oSWtJnzBNCftykzbAvKaNZVHebu5/K//iBTxPmmY+7YhMqkFUVM1C1YTlS3Aeorp1DLXvvm9pbtW0JOMoiX/Hwx6L8EKK+gIOJdVltQWKrnk7gAY8NZKjlVQu8KPcnb5URLwJDzQIDAQAB";

	// �������첽֪ͨҳ��·��  ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String notify_url = "http://ebaotech.natapp1.cc/alibaba/callBack/notify_url";

	// ҳ����תͬ��֪ͨҳ��·�� ��http://��ʽ������·�������ܼ�?id=123�����Զ����������������������������
	public static String return_url = "http://ebaotech.natapp1.cc/alibaba/callBack/return_url";

	// ǩ����ʽ
	public static String sign_type = "RSA2";
	
	// �ַ������ʽ
	public static String charset = "utf-8";
	
	// ֧��������
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
	
	// ֧��������
	public static String log_path = "E:\\";


//�����������������������������������Ļ�����Ϣ������������������������������

    /** 
     * д��־��������ԣ�����վ����Ҳ���ԸĳɰѼ�¼�������ݿ⣩
     * @param sWord Ҫд����־����ı�����
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

