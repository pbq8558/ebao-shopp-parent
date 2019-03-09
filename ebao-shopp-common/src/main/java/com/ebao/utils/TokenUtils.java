package com.ebao.utils;

import java.util.UUID;

import com.ebao.constants.Constants;

public class TokenUtils {

	public static String getMemberToken() {
		return Constants.TOKEN_MEMBER + "-" + UUID.randomUUID();
	}
	public static String getPayToken() {
		return Constants.TOKEN_PAY + "-" + UUID.randomUUID();
	}
}
