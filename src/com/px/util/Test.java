package com.px.util;

import net.sf.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		// 微信预下单 调用示例
		// JSAPIPay();
		// 获取openid 调用示例
		// getOpenid();
		// 企业付款 调用示例
		// compayWxPay();
		// 发送模板消息示例
		// sendTempleMsg();
		// 获取公钥示例
		// getWxPayPublicKey();
		// 付款到银行卡示例
		// compayWxPayBank();
		// 付款到银行卡查询示例
		// compayWxPayBankQuery();
		// 微信退款示例
		// WxPayRefund();
		// 微信退款查询示例
		// WxPayRefundQuery();
	}

	/**
	 * 微信退款查询示例
	 */
	public static void WxPayRefundQuery() {
		try {
			WxPayRefundQueryBuilder wxPayRefundBuilder = new WxPayRefundQueryBuilder();
			wxPayRefundBuilder.setAppid("小程序或公众号APPID");
			wxPayRefundBuilder.setMch_id("商户号");
			wxPayRefundBuilder.setAPI_KEY("商户号APIKEY");
			// 下面四个任选一个
			wxPayRefundBuilder.setOut_trade_no("商家交易订单号");
			wxPayRefundBuilder.setOut_refund_no("退款商家订单号");
			wxPayRefundBuilder.setTransaction_id("微信交易订单号");
			wxPayRefundBuilder.setRefund_id("退款微信订单号");

			wxPayRefundBuilder.build();// 验证数据
			System.out.println(wxPayRefundBuilder.hand());// 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}

	/**
	 * 微信退款示例
	 */
	public static void WxPayRefund() {
		try {
			WxPayRefundBuilder wxPayRefundBuilder = new WxPayRefundBuilder("证书路径");
			wxPayRefundBuilder.setAppid("小程序或公众号APPID");
			wxPayRefundBuilder.setMch_id("商户号");
			wxPayRefundBuilder.setAPI_KEY("商户号APIKEY");
			wxPayRefundBuilder.setTotal_fee(101);// 该订单总金额
			wxPayRefundBuilder.setRefund_fee(101); // 退款金额
			wxPayRefundBuilder.setRefund_desc("测试"); // 退款描述
			// 任选一个
			wxPayRefundBuilder.setOut_trade_no("商家交易订单号");
			wxPayRefundBuilder.setTransaction_id("微信交易订单号");

			wxPayRefundBuilder.build();// 验证数据
			System.out.println(wxPayRefundBuilder.hand());// 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}

	/**
	 * 企业付款到银行卡查询示例
	 */
	public static void compayWxPayBankQuery() {

		try {

			CompanyWxPayBankQueryBuilder wxPayBankQueryBuilder = new CompanyWxPayBankQueryBuilder("支付证书路径",
					"交易订单号（商家，不是微信的）");

			wxPayBankQueryBuilder.setMch_id("商户号");
			wxPayBankQueryBuilder.setAPI_KEY("APIKEY");

			wxPayBankQueryBuilder.build();// 验证数据
			System.out.println(wxPayBankQueryBuilder.hand());// 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}

	/**
	 * 企业付款到银行卡示例
	 */
	public static void compayWxPayBank() {

		try {
			// 获取公钥
			String key = getWxPayPublicKey();

			CompanyWxPayBankBuilder wxPayBankBuilder = new CompanyWxPayBankBuilder("证书路径", key); // key 为微信返回的公钥

			wxPayBankBuilder.setMch_id("商户号");
			wxPayBankBuilder.setAPI_KEY("APIKEY");
			wxPayBankBuilder.setAmount(200); // 支付金额
			wxPayBankBuilder.setDesc("支付描述");
			wxPayBankBuilder.setEnc_bank_no("银行卡卡号");
			wxPayBankBuilder.setEnc_true_name("真实姓名");
			wxPayBankBuilder.setBank_code("银行编码");

			wxPayBankBuilder.build();// 验证数据
			System.out.println(wxPayBankBuilder.hand());// 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取公钥
	 */
	public static String getWxPayPublicKey() {

		try {
			GetPublicKeyBuilder builder = new GetPublicKeyBuilder("证书路径");
			builder.setAPI_KEY("APIKEY");
			builder.setmch_id("微信商户号");
			builder.build();// 验证数据
			JSONObject result = builder.hand();
			System.out.println(result);// 发送处理
			return result.getString("pub_key");
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 微信预下单 调用示例
	 */
	public static void JSAPIPay() {
		try {
			JSAPIWxPayBuilder jsapiWxPayBuilder = new JSAPIWxPayBuilder();
			jsapiWxPayBuilder.setAppid("小程序APPID");
			jsapiWxPayBuilder.setAttach("携带参数");
			jsapiWxPayBuilder.setMch_id("商户号");
			jsapiWxPayBuilder.setAPI_KEY("商户号API秘钥");
			jsapiWxPayBuilder.setBody("商品内容");
			jsapiWxPayBuilder.setTotal_fee(50); // 交易金额
			jsapiWxPayBuilder.setNotify_url("回调地址"); //
			jsapiWxPayBuilder.setSpbill_create_ip("发起请求的IP"); //
			jsapiWxPayBuilder.setOpenid("OPENID");

			jsapiWxPayBuilder.build();// 验证数据
			System.out.println(jsapiWxPayBuilder.hand());// 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取openid 调用示例
	 */
	public static void getOpenid() {
		try {
			GetOpenidBuilder getOpenidBuilder = new GetOpenidBuilder();
			getOpenidBuilder.setAppid("您的小程序Openid");
			getOpenidBuilder.setAppsecret("您的小程序秘钥");
			getOpenidBuilder.setCode("小程序登陆时获取的code");
			getOpenidBuilder.build();// 验证数据
			System.out.println(getOpenidBuilder.hand());// 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}

	/**
	 * 企业付款 调用示例
	 */
	public static void compayWxPay() {
		try {

			CompanyWxPayBuilder compayBuilder = new CompanyWxPayBuilder("证书路径");
			compayBuilder.setMch_appid("被付款人使用的小程序appid或公众号appid");
			compayBuilder.setAPI_KEY("您的商户号API操作秘钥");
			compayBuilder.setDesc("付款备注");
			compayBuilder.setMchid("您的商户号");
			compayBuilder.setOpenid("小程序或公众号对应的openid");
			compayBuilder.setSpbill_create_ip("本机ip，不能是 localhost或127.0.0.1");
			compayBuilder.setAmount(200); // 支付金额

			compayBuilder.build(); // 验证数据
			System.out.println(compayBuilder.hand()); // 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送模板消息示例
	 */
	public static void sendTempleMsg() {
		try {
			SendTempleMsgBuilder sendTempleMsgBuilder = new SendTempleMsgBuilder("公众号APPID", "发送模板ID");
			sendTempleMsgBuilder.setAccess_token("access——token");
			sendTempleMsgBuilder.toMiniprogram("用户点击模板消息时跳转小程序的APPID", "跳转路径");
			sendTempleMsgBuilder.toUrl("用户点击模板消息时跳转的网址");
			sendTempleMsgBuilder.addCostomData("自定义数据名称", "十六进制颜色值", "具体数值"); // 添加自定义数据
			// 示例
			sendTempleMsgBuilder.addCostomData("first", "#ff0000", "您有新的故障通知");
			sendTempleMsgBuilder.build(); // 验证数据
			System.out.println(sendTempleMsgBuilder.hand()); // 发送处理
		} catch (LackParamExceptions e) {
			e.printStackTrace();
		}
	}
}
