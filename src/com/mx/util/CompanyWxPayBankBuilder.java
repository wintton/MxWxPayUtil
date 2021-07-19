package com.mx.util;

import java.util.Map;

import net.sf.json.JSONObject;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
public class CompanyWxPayBankBuilder implements WxPayDataBuilder {

	private static String sendUrl = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";

	private String certPath, API_KEY, mch_id, nonce_str, partner_trade_no, desc, sign, enc_bank_no, enc_true_name,
			bank_code, enc_bank_no_pwd, enc_true_name_pwd;
	private String pub_key;
	private int amount;
	private StringBuffer reStringBuffer;
	private StringBuffer signStringBuffer;
	private boolean build = false;

	public CompanyWxPayBankBuilder(String certPath, String pub_key) {
		nonce_str = WxPayUtil.getRandomStr(20);
		int startIndex = pub_key.indexOf("-----BEGIN RSA PUBLIC KEY-----");
		int endIndex = pub_key.indexOf("-----END RSA PUBLIC KEY-----");
		this.pub_key = pub_key;

		if (startIndex >= 0 && endIndex >= 0) {
			this.pub_key = pub_key.substring(startIndex + "-----BEGIN RSA PUBLIC KEY-----".length(), endIndex);
		}

		this.certPath = certPath;
		partner_trade_no = System.currentTimeMillis() + WxPayUtil.getRandomStr(8);
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
		CompanyWxPayBankBuilder.sendUrl = sendUrl;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getEnc_bank_no() {
		return enc_bank_no;
	}

	public void setEnc_bank_no(String enc_bank_no) {
		try {
			enc_bank_no_pwd = RSAEncryp.encrypt(enc_bank_no.getBytes("utf-8"), pub_key, 11,
					"RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING");
		} catch (Exception e) {
			e.printStackTrace();
			enc_bank_no_pwd = "";
		}
		this.enc_bank_no = enc_bank_no;
	}

	public String getEnc_true_name() {
		return enc_true_name;
	}

	public void setEnc_true_name(String enc_true_name) {
		try {
			enc_true_name_pwd = RSAEncryp.encrypt(enc_true_name.getBytes("utf-8"), pub_key, 11,
					"RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING");
		} catch (Exception e) {
			e.printStackTrace();
			enc_true_name_pwd = "";
		}
		this.enc_true_name = enc_true_name;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	@Override
	public boolean build() throws LackParamExceptions {
		build = false;
		reStringBuffer = new StringBuffer();
		signStringBuffer = new StringBuffer();
		reStringBuffer.append("<xml>");
		appendParam("amount", amount + "", true);
		appendParam("bank_code", bank_code + "", true);
		appendParam("desc", desc, true);
		appendParam("enc_bank_no", enc_bank_no_pwd, true);
		appendParam("enc_true_name", enc_true_name_pwd, true);
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

		if (pub_key == null || pub_key.length() == 0) {
			throw new LackParamExceptions("未设置公钥");
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
