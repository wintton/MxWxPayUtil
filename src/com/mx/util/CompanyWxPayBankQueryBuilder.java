package com.mx.util;

import java.util.Map;

import net.sf.json.JSONObject;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class CompanyWxPayBankQueryBuilder implements WxPayDataBuilder {
	private static String sendUrl = "https://api.mch.weixin.qq.com/mmpaysptrans/query_bank";

	private String certPath, API_KEY, mch_id, nonce_str, partner_trade_no, sign;
	private StringBuffer reStringBuffer;
	private StringBuffer signStringBuffer;
	private boolean build = false;

	public CompanyWxPayBankQueryBuilder(String certPath, String partner_trade_no) {
		nonce_str = WxPayUtil.getRandomStr(20);
		this.certPath = certPath;
		this.partner_trade_no = partner_trade_no;
	}

	public static String getSendUrl() {
		return sendUrl;
	}

	public static void setSendUrl(String sendUrl) {
		CompanyWxPayBankQueryBuilder.sendUrl = sendUrl;
	}

	public String getCertPath() {
		return certPath;
	}

	public void setCertPath(String certPath) {
		this.certPath = certPath;
	}

	public String getAPI_KEY() {
		return API_KEY;
	}

	public void setAPI_KEY(String aPI_KEY) {
		API_KEY = aPI_KEY;
	}

	public String getmch_id() {
		return mch_id;
	}

	public void setmch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getPartner_trade_no() {
		return partner_trade_no;
	}

	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	@Override
	public boolean build() throws LackParamExceptions {
		build = false;
		reStringBuffer = new StringBuffer();
		signStringBuffer = new StringBuffer();
		reStringBuffer.append("<xml>");
		appendParam("mch_id", mch_id, true);
		appendParam("nonce_str", nonce_str, true);
		appendParam("partner_trade_no", partner_trade_no, true);
		getSign(API_KEY, this.signStringBuffer.toString());
		appendParam("sign", sign, true);
		reStringBuffer.append("</xml>");
		build = true;
		return true;
	}

	@SuppressWarnings("null")
	@Override
	public JSONObject hand() throws LackParamExceptions {
		if (!build) {
			throw new LackParamExceptions("未build成功，请先确认build成功后再运行");
		}

		if (certPath == null || certPath.length() == 0) {
			throw new LackParamExceptions("未设置证书路径");
		}

		String result = "";
		JSONObject resultJson = new JSONObject();

		try {

			result = WxPayUtil.doPostDataWithCert(sendUrl, reStringBuffer.toString(), mch_id, certPath);

			Map<String, String> getResult = WxPayUtil.xmlToMap(result);
			if (getResult.get("result_code") == null || !"SUCCESS".equalsIgnoreCase(getResult.get("result_code"))) {
				resultJson.put("return_code", "FAIL");
				resultJson.put("return_msg", getResult.get("return_msg"));
				resultJson.put("err_code_des", getResult.get("err_code_des"));
				resultJson.put("err_code", getResult.get("err_code"));
			} else {
				resultJson.put("return_code", "SUCCESS");
				resultJson.put("err_code_des", getResult.get("err_code_des"));
				resultJson.put("err_code", getResult.get("err_code"));
				resultJson.put("return_msg", getResult.get("return_msg"));
				resultJson.put("amount", getResult.get("amount"));
				resultJson.put("payment_no", getResult.get("payment_no"));
				resultJson.put("cmms_amt", getResult.get("cmms_amt"));
				resultJson.put("partner_trade_no", getResult.get("partner_trade_no"));
				resultJson.put("nonce_str", getResult.get("nonce_str"));
				resultJson.put("mch_id", getResult.get("mch_id"));
				resultJson.put("mch_appid", getResult.get("mch_appid"));
				resultJson.put("status", getResult.get("status"));
				resultJson.put("create_time", getResult.get("create_time"));
				resultJson.put("reason", getResult.get("reason"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resultJson;
	}

	private void getSign(String apikey, String value) throws LackParamExceptions {
		if (apikey == null) {
			throw new LackParamExceptions("商户号API秘钥不能为空");
		} else {
			sign = WxPayUtil.MD5(signStringBuffer.toString() + "key=" + apikey);
		}
	}

	private void appendParam(String key, String value, boolean isneed) throws LackParamExceptions {

		if (value == null || value.length() == 0) {
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

}
