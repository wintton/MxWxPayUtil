package com.px.util;

import java.io.IOException;
import java.util.Map;

import net.sf.json.JSONObject;

public class JSAPIWxPayBuilder implements WxPayDataBuilder {

	private static String sendUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	private static String sendUrl2 = "https://api2.mch.weixin.qq.com/pay/unifiedorder";

	private String appid, mch_id, body, out_trade_no, device_info, sign, detail, attach, time_start, time_expire,
			goods_tag, product_id, spbill_create_ip, notify_url, limit_pay, openid, receipt, scene_info, API_KEY,
			nonce_str;

	private int total_fee;
	private StringBuffer reStringBuffer;
	private StringBuffer signStringBuffer;
	private boolean build = false;

	public JSAPIWxPayBuilder() {
		nonce_str = WxPayUtil.getRandomStr(20);
		// 默认订单时间为半小时内有效
		time_start = WxPayUtil.FormatDate(System.currentTimeMillis());
		time_expire = WxPayUtil.FormatDate(System.currentTimeMillis() + 30 * 60 * 1000);

		out_trade_no = System.currentTimeMillis() + WxPayUtil.getRandomStr(8);
	}

	public static String getSendUrl() {
		return sendUrl;
	}

	public static void setSendUrl(String sendUrl) {
		JSAPIWxPayBuilder.sendUrl = sendUrl;
	}

	public static String getSendUrl2() {
		return sendUrl2;
	}

	public static void setSendUrl2(String sendUrl2) {
		JSAPIWxPayBuilder.sendUrl2 = sendUrl2;
	}

	public String getAPI_KEY() {
		return API_KEY;
	}

	public void setAPI_KEY(String aPI_KEY) {
		API_KEY = aPI_KEY;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getTime_start() {
		return time_start;
	}

	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}

	public String getTime_expire() {
		return time_expire;
	}

	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}

	public String getGoods_tag() {
		return goods_tag;
	}

	public void setGoods_tag(String goods_tag) {
		this.goods_tag = goods_tag;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getScene_info() {
		return scene_info;
	}

	public void setScene_info(String scene_info) {
		this.scene_info = scene_info;
	}

	public int getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}

	@Override
	public boolean build() throws LackParamExceptions {
		build = false;
		reStringBuffer = new StringBuffer();
		signStringBuffer = new StringBuffer();

		reStringBuffer.append("<xml>");
		appendParam("appid", appid, true);
		appendParam("attach", attach, false);
		appendParam("body", body, true);
		appendParam("detail", detail, false);
		appendParam("device_info", device_info, false);
		appendParam("goods_tag", goods_tag, false);
		appendParam("limit_pay", limit_pay, false);
		appendParam("mch_id", mch_id, true);
		appendParam("nonce_str", nonce_str, true);
		appendParam("notify_url", notify_url, true);
		appendParam("openid", openid, true);
		appendParam("out_trade_no", out_trade_no, true);
		appendParam("product_id", product_id, false);
		appendParam("receipt", receipt, false);
		appendParam("scene_info", scene_info, false);
		appendParam("sign_type", "MD5", false);
		appendParam("spbill_create_ip", spbill_create_ip, true);
		appendParam("time_expire", time_expire, false);
		appendParam("time_start", time_start, false);
		appendParam("total_fee", total_fee + "", true);
		appendParam("trade_type", "JSAPI", true);

		getSign(API_KEY, this.signStringBuffer.toString());
		appendParam("sign", sign, true);
		reStringBuffer.append("</xml>");

		build = true;

		return build;
	}

	private void getSign(String apikey, String value) throws LackParamExceptions {
		if (apikey == null) {
			throw new LackParamExceptions("商户号操作秘钥不能为空");
		} else {

			sign = WxPayUtil.MD5(signStringBuffer.toString() + "key=" + API_KEY);
		}
	}

	private void appendParam(String key, String value, boolean isneed) throws LackParamExceptions {

		if (value == null) {
			if (isneed) {
				throw new LackParamExceptions("参数" + key + "不能为空");
			} else {
				return;
			}
		}
		this.reStringBuffer.append("<" + key + ">");
		this.reStringBuffer.append("<![CDATA[" + value + "]]>");
		this.reStringBuffer.append("</" + key + ">");
		this.signStringBuffer.append(key + "=" + value + "&");
	}

	private void signAppendParam(String key, String value, boolean isneed) throws LackParamExceptions {
		if (value == null) {
			if (isneed) {
				throw new LackParamExceptions("参数" + key + "不能为空");
			} else {
				return;
			}
		}
		this.signStringBuffer.append(key + "=" + value + "&");
	}

	@Override
	public JSONObject hand() throws LackParamExceptions {
		if (!build) {
			throw new LackParamExceptions("未build成功，请先确认build成功后再运行");
		}
		String result = "";
		try {
			// 正常是的域名
			result = WxPayUtil.sendHttpsRequest(sendUrl, reStringBuffer.toString(), "text/xml", "utf-8", "POST");
		} catch (IOException e) {
			e.printStackTrace();
			// 备用域名
			try {
				result = WxPayUtil.sendHttpsRequest(sendUrl2, reStringBuffer.toString(), "text/xml", "utf-8", "POST");
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new LackParamExceptions(e1.toString());
			}
		}

		Map<String, String> getResult = WxPayUtil.xmlToMap(result);

		JSONObject resultJson = new JSONObject();

		if (getResult.get("result_code") == null || !"SUCCESS".equalsIgnoreCase(getResult.get("result_code"))) {
			resultJson.put("return_code", "FAIL");
			resultJson.put("return_msg", getResult.get("return_msg"));
			resultJson.put("err_code", getResult.get("err_code"));
			resultJson.put("err_code_des", getResult.get("err_code_des"));
		} else {
			long curTime = System.currentTimeMillis() / 1000;
			// 再次签名
			signStringBuffer = new StringBuffer();
			signAppendParam("appId", appid, true);
			signAppendParam("nonceStr", getResult.get("nonce_str"), true);
			signAppendParam("package", "prepay_id=" + getResult.get("prepay_id"), true);
			signAppendParam("signType", "MD5", true);
			signAppendParam("timeStamp", curTime + "", true);

			String sign = WxPayUtil.MD5(signStringBuffer.toString() + "key=" + API_KEY);

			resultJson.put("return_code", "SUCCESS");
			resultJson.put("return_msg", getResult.get("return_msg"));
			resultJson.put("err_code", getResult.get("err_code"));
			resultJson.put("err_code_des", getResult.get("err_code_des"));
			resultJson.put("nonceStr", getResult.get("nonce_str"));
			resultJson.put("timeStamp", curTime);
			resultJson.put("paySign", sign);
			resultJson.put("package", "prepay_id=" + getResult.get("prepay_id"));
		}

		return resultJson;
	}

}
