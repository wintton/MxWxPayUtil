package com.px.util;

/**
 * 梦辛工作室
 * 
 * @author 灵
 * @version 1.0.2
 *
 */
public class Test {
	public static void main(String[] args) {
		// 微信预下单 调用示例
		JSAPIPay();
		// 获取openid 调用示例
		// getOpenid();
		// 企业付款 调用示例
		// compayWxPay();
		// 发送模板消息示例
		// sendTempleMsg();
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
			// System.out.println(jsapiWxPayBuilder.hand());// 发送处理
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
