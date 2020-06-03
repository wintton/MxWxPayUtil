package com.px.util;

import java.util.Map;

import net.sf.json.JSONObject;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class CompanyWxPayBuilder implements WxPayDataBuilder {

	private static String sendUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

	private String certPath, API_KEY, mch_appid, mchid, nonce_str, partner_trade_no, desc, check_name, spbill_create_ip,
			openid, device_info, re_user_name, sign;

	private int amount;
	private StringBuffer reStringBuffer;
	private StringBuffer signStringBuffer;
	private boolean build = false;

	public CompanyWxPayBuilder(String certPath) {
		nonce_str = WxPayUtil.getRandomStr(20);
		this.certPath = certPath;
		// 默认订单时间为半小时内有效
		partner_trade_no = System.currentTimeMillis() + WxPayUtil.getRandomStr(8);
		check_name = "NO_CHECK";
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public static String getSendUrl() {
		return sendUrl;
	}

	public static void setSendUrl(String sendUrl) {
		CompanyWxPayBuilder.sendUrl = sendUrl;
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

	public String getMch_appid() {
		return mch_appid;
	}

	public void setMch_appid(String mch_appid) {
		this.mch_appid = mch_appid;
	}

	public String getMchid() {
		return mchid;
	}

	public void setMchid(String mchid) {
		this.mchid = mchid;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCheck_name() {
		return check_name;
	}

	public void setCheck_name(String check_name) {
		this.check_name = check_name;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}

	public String getRe_user_name() {
		return re_user_name;
	}

	public void setRe_user_name(String re_user_name) {
		this.re_user_name = re_user_name;
	}

	@Override
	public boolean build() throws LackParamExceptions {
		build = false;
		reStringBuffer = new StringBuffer();
		signStringBuffer = new StringBuffer();

		reStringBuffer.append("<xml>");
		appendParam("amount", amount + "", true);
		appendParam("check_name", check_name, true);
		appendParam("desc", desc, true);
		appendParam("device_info", device_info, false);
		appendParam("mch_appid", mch_appid, true);
		appendParam("mchid", mchid, true);
		appendParam("nonce_str", nonce_str, true);
		appendParam("openid", openid, true);
		appendParam("partner_trade_no", partner_trade_no, true);
		appendParam("re_user_name", re_user_name, false);
		appendParam("spbill_create_ip", spbill_create_ip, true);

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
			result = WxPayUtil.doPostDataWithCert(sendUrl, reStringBuffer.toString(), mchid, certPath);

			Map<String, String> getResult = WxPayUtil.xmlToMap(result);
			if (getResult.get("result_code") == null || !"SUCCESS".equalsIgnoreCase(getResult.get("result_code"))) {
				resultJson.put("return_code", "FAIL");
				resultJson.put("return_msg", getResult.get("return_msg"));
				resultJson.put("err_code_des", getResult.get("err_code_des"));
				resultJson.put("err_code", getResult.get("err_code"));
			} else {
				resultJson.put("return_code", "SUCCESS");
				resultJson.put("return_msg", getResult.get("return_msg"));
				resultJson.put("payment_time", getResult.get("payment_time"));
				resultJson.put("payment_no", getResult.get("payment_no"));
				resultJson.put("partner_trade_no", getResult.get("partner_trade_no"));
				resultJson.put("nonce_str", getResult.get("nonce_str"));
				resultJson.put("mchid", getResult.get("mchid"));
				resultJson.put("mch_appid", getResult.get("mch_appid"));
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

}
