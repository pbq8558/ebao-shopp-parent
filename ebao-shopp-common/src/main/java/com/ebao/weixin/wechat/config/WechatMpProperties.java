package com.ebao.weixin.wechat.config;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * wechat mp properties
 *
 * @author Binary Wang(https://github.com/binarywang)
 */
@ConfigurationProperties(prefix = "wechat.mp")
public class WechatMpProperties {
    /**
     * ����΢�Ź��ںŵ�appid
     */
    private String appId;

    /**
     * ����΢�Ź��ںŵ�app secret
     */
    private String secret;

    /**
     * ����΢�Ź��ںŵ�token
     */
    private String token;

    /**
     * ����΢�Ź��ںŵ�EncodingAESKey
     */
    private String aesKey;

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAesKey() {
        return this.aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }
}
