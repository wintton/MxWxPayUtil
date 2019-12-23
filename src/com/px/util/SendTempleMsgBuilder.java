package com.px.util;

import java.io.IOException;

import net.sf.json.JSONObject;

public class SendTempleMsgBuilder implements WxPayDataBuilder {

	private String appid, secret;
	private static String sendUrl = "https://api.weixin.qq.com/cgi-bin/token";
	private static String sendUrl2 = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
	private String access_token;
	private JSONObject sendjson; // 最后发送的数据
	private JSONObject data; // 自定义数据
	private boolean build = false;
	private StringBuffer reStringBuffer;

	/**
	 * 
	 * @param toUserOpenid
	 *            接收人的openid
	 * @param template_id
	 *            发送的模板ID
	 */
	public SendTempleMsgBuilder(String toUserOpenid, String template_id) {
		// TODO Auto-generated constructor stub
		sendjson = new JSONObject();
		data = new JSONObject();
		sendjson.put("touser", toUserOpenid);
		sendjson.put("template_id", template_id);
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public static String getSendUrl() {
		return sendUrl;
	}

	public static void setSendUrl(String sendUrl) {
		SendTempleMsgBuilder.sendUrl = sendUrl;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * 用户点击模板消息后跳转小程序
	 * 
	 * @param appid
	 *            跳转小程序的APPID
	 * @param pagepath
	 *            跳转的页面
	 */
	public void toMiniprogram(String appid, String pagepath) {
		JSONObject minidata = new JSONObject();
		minidata.put("appid", appid);
		minidata.put("pagepath", pagepath);
		sendjson.put("miniprogram", minidata);
	}

	/**
	 * 用户点击模板消息后跳转网页
	 * 
	 * @param url
	 *            网页地址
	 */
	public void toUrl(String url) {
		sendjson.put("url", url);
	}

	/**
	 * 添加自定义参数
	 * 
	 * @param name
	 *            自定义参数名称
	 * @param color
	 *            十六进制颜色值
	 * @param value
	 *            具体的数据
	 */
	public void addCostomData(String name, String color, String value) {
		JSONObject infodata = new JSONObject();
		infodata.put("value", value);
		infodata.put("color", color);
		data.put(name, infodata);
	}

	@Override
	public boolean build() throws LackParamExceptions {
		build = false;
		appendParam("appid", appid, true);
		appendParam("secret", secret, true);
		appendParam("grant_type", "client_credential", true);
		reStringBuffer.deleteCharAt(reStringBuffer.length() - 1);
		build = true;
		return build;
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
	public JSONObject hand() throws LackParamExceptions {
		if (!build) {
			throw new LackParamExceptions("未build成功，请先确认build成功后再运行");
		}
		String result = "";
		JSONObject jsonObject = null;
		try {
			if (access_token == null) {
				// 获取access_token
				result = WxPayUtil.sendHttpsRequest(sendUrl, reStringBuffer.toString(), "text/xml", "utf-8", "GET");
				jsonObject = JSONObject.fromObject(result);
				if (jsonObject.getString("access_token") == null) {
					jsonObject.put("return_code", "FAIL");
					jsonObject.put("return_msg", "获取token失败");
					return jsonObject;
				} else {
					access_token = jsonObject.getString("access_token");
				}
			}
			result = WxPayUtil.sendHttpsRequest(sendUrl2 + access_token, sendjson.toString(), "text/xml", "utf-8",
					"POST");
			jsonObject = JSONObject.fromObject(result);

		} catch (IOException e) {
			e.printStackTrace();
			// 备用域名
			throw new LackParamExceptions(e.toString());
		}

		return jsonObject;
	}

}
