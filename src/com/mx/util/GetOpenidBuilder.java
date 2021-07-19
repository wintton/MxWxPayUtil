package com.mx.util;

import java.io.IOException;

import net.sf.json.JSONObject;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class GetOpenidBuilder implements WxPayDataBuilder {

	private String appid, appsecret, code;
	private static String sendUrl = "https://api.weixin.qq.com/sns/oauth2/access_token";
	private StringBuffer reStringBuffer;
	private boolean build = false;

	public static String getSendUrl() {
		return sendUrl;
	}

	public static void setSendUrl(String sendUrl) {
		GetOpenidBuilder.sendUrl = sendUrl;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppsecret() {
		return appsecret;
	}

	public void setAppsecret(String appsecret) {
		this.appsecret = appsecret;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	private void appendParam(String key, String value, boolean isneed) throws LackParamExceptions {

		if (value == null) {
			if (isneed) {
				throw new LackParamExceptions("参数" + key + "不能为空");
			} else {
				return;
			}
		}
		this.reStringBuffer.append(key + "=" + value + "&");
	}

	@Override
	public boolean build() throws LackParamExceptions {
		// TODO Auto-generated method stub
		build = false;
		reStringBuffer = new StringBuffer();
		appendParam("appid", appid, true);
		appendParam("secret", appsecret, true);
		appendParam("code", code, true);
		appendParam("grant_type", "authorization_code", true);
		reStringBuffer.deleteCharAt(reStringBuffer.length() - 1);
		build = true;

		return build;
	}

	@Override
	public JSONObject hand() throws LackParamExceptions {
		if (!build) {
			throw new LackParamExceptions("未build成功，请先确认build成功后再运行");
		}
		String result = "";
		JSONObject jsonObject = null;
		try {
			// 正常是的域名
			result = WxPayUtil.sendHttpsRequest(sendUrl, reStringBuffer.toString(), "text/xml", "utf-8", "GET");
			jsonObject = JSONObject.fromObject(result);
		} catch (IOException e) {
			e.printStackTrace();
			// 备用域名
			throw new LackParamExceptions(e.toString());
		}
		return jsonObject;
	}

}
