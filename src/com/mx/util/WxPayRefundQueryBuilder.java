package com.mx.util;

import java.util.Map;

import net.sf.json.JSONObject;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class WxPayRefundQueryBuilder implements WxPayDataBuilder {

	private static String sendUrl = "https://api.mch.weixin.qq.com/pay/refundquery";

	private String appid, mch_id, transaction_id, out_refund_no, out_trade_no, refund_id, sign, API_KEY, nonce_str;

	private StringBuffer reStringBuffer;
	private StringBuffer signStringBuffer;
	private boolean build = false;

	public WxPayRefundQueryBuilder() {
		nonce_str = WxPayUtil.getRandomStr(20);
	}

	public StringBuffer getReStringBuffer() {
		return reStringBuffer;
	}

	public void setReStringBuffer(StringBuffer reStringBuffer) {
		this.reStringBuffer = reStringBuffer;
	}

	public StringBuffer getSignStringBuffer() {
		return signStringBuffer;
	}

	public void setSignStringBuffer(StringBuffer signStringBuffer) {
		this.signStringBuffer = signStringBuffer;
	}

	public static String getSendUrl() {
		return sendUrl;
	}

	public static void setSendUrl(String sendUrl) {
		WxPayRefundQueryBuilder.sendUrl = sendUrl;
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

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_refund_no() {
		return out_refund_no;
	}

	public void setOut_refund_no(String out_refund_no) {
		this.out_refund_no = out_refund_no;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getAPI_KEY() {
		return API_KEY;
	}

	public void setAPI_KEY(String aPI_KEY) {
		API_KEY = aPI_KEY;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public boolean isBuild() {
		return build;
	}

	public void setBuild(boolean build) {
		this.build = build;
	}

	@Override
	public boolean build() throws LackParamExceptions {
		build = false;
		reStringBuffer = new StringBuffer();
		signStringBuffer = new StringBuffer();

		reStringBuffer.append("<xml>");
		appendParam("appid", appid, true);
		appendParam("mch_id", mch_id, true);
		appendParam("nonce_str", nonce_str, true);
		appendParam("out_refund_no", out_refund_no, false);
		appendParam("out_trade_no", out_trade_no, false);
		appendParam("refund_id", refund_id, false);
		appendParam("sign_type", "MD5", false);
		appendParam("transaction_id", transaction_id, false);
		getSign(API_KEY, this.signStringBuffer.toString());
		appendParam("sign", sign.toUpperCase(), true);

		reStringBuffer.append("</xml>");

		build = true;

		return build;
	}

	private void getSign(String apikey, String value) throws LackParamExceptions {
		if (apikey == null) {
			throw new LackParamExceptions("商户号操作秘钥不能为空");
		} else {
			sign = WxPayUtil.MD5(value + "key=" + API_KEY);
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

	@Override
	public JSONObject hand() throws LackParamExceptions {
		if (!build) {
			throw new LackParamExceptions("未build成功，请先确认build成功后再运行");
		}
		String result = "";
		JSONObject resultJson = new JSONObject();
		try {
			result = WxPayUtil.sendHttpsRequest(sendUrl, reStringBuffer.toString(), "text/xml", "utf-8", "POST");

			Map<String, String> getResult = WxPayUtil.xmlToMap(result);

			if (getResult.get("result_code") == null || !"SUCCESS".equalsIgnoreCase(getResult.get("result_code"))) {
				resultJson.put("return_code", "FAIL");
				resultJson.put("return_msg", getResult.get("return_msg"));
				resultJson.put("err_code", getResult.get("err_code"));
				resultJson.put("err_code_des", getResult.get("err_code_des"));
			} else {
				resultJson.put("return_code", "SUCCESS");
				resultJson.put("return_msg", getResult.get("return_msg"));
				resultJson.put("err_code", getResult.get("err_code"));
				resultJson.put("err_code_des", getResult.get("err_code_des"));
				resultJson.put("out_refund_no", getResult.get("out_refund_no"));
				resultJson.put("refund_count", getResult.get("refund_count"));
				resultJson.put("transaction_id", getResult.get("transaction_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultJson;
	}

}
