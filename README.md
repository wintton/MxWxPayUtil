# MxWxPayUtil 微信支付助手
#包含 微信预下单 获取openid  企业付款 发送模板 获取公钥 付款到银行卡 付款到银行卡查询 微信退款 微信退款查询 封装
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

大家好，我是梦辛工作室的灵，近期在制作相关软件的过程中，有需要用到对微信商户号的投诉风险合规，第一次搞这个还是踩了不少坑，然后为了方便使用做了以下封装便于大家使用，其实也不难，主要怪自己没有去仔细看微信提供的指导（在 官方提供的 wechatpay-apache-httpclient-0.2.2.jar 的基础上继续简化的），下来就讲下怎么写吧

 先准备好以下相关资料：
 1.商户号 编号
 2.商户号 ApiV3 秘钥，需要在 微信商户号后台设置
 3.商户号证书私钥，下载商户号 证书时会有那个 apiclient_key.pem 文件就是它
 4.商户号证书序列号，这个可以在 微信商户号证书管理后台查看
 
准备好以上资料后就可以开始写了
第一步：加载证书，获取到httpCilent对象：

```java
public void setup() {

		if (!doCheckParam()) {
			isStep = false;
			return;
		}

		try {

			privateKey = new String(Files.readAllBytes(Paths.get(privateKeyFilePath)), "utf-8");

			// 加载商户私钥（privateKey：私钥字符串）
			merchantPrivateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes("utf-8")));

			// 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
			AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
					new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)),
					apiV3Key.getBytes("utf-8"));

			// 初始化httpClient
			httpClient = WechatPayHttpClientBuilder.create().withMerchant(mchId, mchSerialNo, merchantPrivateKey)
					.withValidator(new WechatPay2Validator(verifier)).build();

			isStep = true;

		} catch (Exception e) {
			// TODO: handle exception
			errorHint = errorHint.toString();
			isStep = false;
		}

	}
```
第二步：发起创建投诉回调通知 ，其他操作类同

```java
/**
	 * 创建投诉回调通知
	 * 
	 * @param url
	 *            回调地址
	 * @return
	 * @throws Exception
	 */
	public String CreateComplaintsNotify(String url) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/merchant-service/complaint-notifications");
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("url", url);

		StringEntity entity = new StringEntity(dataJSON.toString());
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpPost);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}
```

这里需要注意的是：微信提供的这个 API封装（wechatpay-apache-httpclient-0.2.2.jar）在发起请求时已经自带了 Authorization Header，所以不用再去添加这个Header，不然会报错，楼主在这里被坑了好久，翻了不好文档才找到

还有就是如果有出现 java.security.InvalidKeyException: Illegal key size WechatPayHttpClientBuild 错误的话，请注意 java版本，如果在 java 8u 162 以下的话，请更换 java 版本 ，必须要在 java 8u 162 以上才可以 ， 早期的java 运行 限制了 JCE支持的秘钥长度，即默认不支持256位的AES，这里楼主也被坑了好早，试过好多方法才找到，换来是java版本低了

下面是所有代码示例：

```java
package com.mx.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.WechatPayUploadHttpPost;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class WeChatAPIV3 {

	CloseableHttpClient httpClient;
	String mchId = "商户号";
	String mchSerialNo = "证书序列号";
	String apiV3Key = "APIV3秘钥";
	String privateKeyFilePath = "证书私钥路径";

	String privateKey = "";
	PrivateKey merchantPrivateKey;
	String errorHint = "";

	public String getMchId() {
		return mchId;
	}

	public void setMchId(String mchId) {
		this.mchId = mchId;
	}

	public String getMchSerialNo() {
		return mchSerialNo;
	}

	public void setMchSerialNo(String mchSerialNo) {
		this.mchSerialNo = mchSerialNo;
	}

	public String getApiV3Key() {
		return apiV3Key;
	}

	public void setApiV3Key(String apiV3Key) {
		this.apiV3Key = apiV3Key;
	}

	public String getPrivateKeyFilePath() {
		return privateKeyFilePath;
	}

	public void setPrivateKeyFilePath(String privateKeyFilePath) {
		this.privateKeyFilePath = privateKeyFilePath;
	}

	private boolean isStep = false;

	public WeChatAPIV3() {

	}

	public WeChatAPIV3(String mchId, String mchSerialNo, String apiV3Key, String privateKeyFilePath) {
		this.mchId = mchId;
		this.mchSerialNo = mchSerialNo;
		this.apiV3Key = apiV3Key;
		this.privateKeyFilePath = privateKeyFilePath;

	}

	public String rsaDecryptOAEP(String ciphertext) throws BadPaddingException, IOException {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		if (merchantPrivateKey == null) {
			errorHint = "未成功加载私钥";
			return null;
		}

		try {

			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");

			cipher.init(Cipher.DECRYPT_MODE, merchantPrivateKey);

			byte[] data = Base64.getDecoder().decode(ciphertext);
			return new String(cipher.doFinal(data), "utf-8");
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("无效的私钥", e);
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			throw new BadPaddingException("解密失败");
		}
	}

	

	public boolean doCheckParam() {

		return doCheckValue(mchId, mchSerialNo, apiV3Key, privateKeyFilePath);
	}

	public boolean doCheckValue(String... item) {
		for (String each : item) {
			if (each == null || each.length() == 0) {
				errorHint = "缺少必要参数";
				return false;
			}
		}
		return true;
	}

	public void setup() {

		if (!doCheckParam()) {
			isStep = false;
			return;
		}

		try {

			privateKey = new String(Files.readAllBytes(Paths.get(privateKeyFilePath)), "utf-8");

			// 加载商户私钥（privateKey：私钥字符串）
			merchantPrivateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes("utf-8")));

			// 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
			AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
					new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)),
					apiV3Key.getBytes("utf-8"));

			// 初始化httpClient
			httpClient = WechatPayHttpClientBuilder.create().withMerchant(mchId, mchSerialNo, merchantPrivateKey)
					.withValidator(new WechatPay2Validator(verifier)).build();

			isStep = true;

		} catch (Exception e) {
			// TODO: handle exception
			errorHint = errorHint.toString();
			isStep = false;
		}

	}

	/**
	 * 查询投诉详情
	 * 
	 * @param complaint_id
	 *            投诉单号
	 * @return
	 * @throws Exception
	 */
	public String GetComplaintsInfo(String complaint_id) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpGet httpGet = new HttpGet(
				"https://api.mch.weixin.qq.com/v3/merchant-service/complaints-v2/" + complaint_id);
		httpGet.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpGet);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}

	/**
	 * 查询协商历史
	 * 
	 * @param complaint_id
	 *            投诉单号
	 * @param offset
	 *            开始位置
	 * @param limit
	 *            返回数据条数
	 * @return
	 * @throws Exception
	 */
	public String GetComplaintsHis(String complaint_id, int offset, int limit) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpGet httpGet = new HttpGet("https://api.mch.weixin.qq.com/v3/merchant-service/complaints-v2/" + complaint_id
				+ "/negotiation-historys?limit=" + limit + "&offset=" + offset);
		httpGet.setHeader("Accept", "application/json");
		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpGet);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}

	/**
	 * 查询投诉单列表
	 * 
	 * @param offset
	 *            开始位置
	 * @param limit
	 *            返回数据条数
	 * @param begin_date
	 *            开始日期 yyyy-MM-dd
	 * @param end_date
	 *            结束日期 yyyy-MM-dd 时间差最大一个月
	 * @return 查询结果
	 * @throws Exception
	 */
	public String GetComplaintsList(int offset, int limit, String begin_date, String end_date) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpGet httpGet = new HttpGet(
				"https://api.mch.weixin.qq.com/v3/merchant-service/complaints-v2?limit=" + limit + "&offset=" + offset
						+ "&begin_date=" + begin_date + "&end_date=" + end_date + "&complainted_mchid=" + mchId);
		httpGet.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpGet);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}

	public void after() throws IOException {
		httpClient.close();
	}

	/**
	 * 创建投诉回调通知
	 * 
	 * @param url
	 *            回调地址
	 * @return
	 * @throws Exception
	 */
	public String CreateComplaintsNotify(String url) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/merchant-service/complaint-notifications");
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("url", url);

		StringEntity entity = new StringEntity(dataJSON.toString());
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpPost);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}

	/**
	 * 更新投诉回调通知
	 * 
	 * @param url
	 *            回调通知
	 * @return
	 * @throws Exception
	 */
	public String UpdateComplaintsNotify(String url) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPut httpPut = new HttpPut("https://api.mch.weixin.qq.com/v3/merchant-service/complaint-notifications");
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("url", url);

		StringEntity entity = new StringEntity(dataJSON.toString());
		entity.setContentType("application/json");
		httpPut.setEntity(entity);
		httpPut.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpPut);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;

	}

	/**
	 * 删除投诉回调通知地址
	 * 
	 * @return
	 * @throws Exception
	 */
	public String DelComplaintsNotify() throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpDelete httpDel = new HttpDelete(
				"https://api.mch.weixin.qq.com/v3/merchant-service/complaint-notifications");
		httpDel.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpDel);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}

	/**
	 * 提交回复
	 * 
	 * @param complaint_id
	 *            被投诉单号
	 * @param response_content
	 *            回复内容
	 * @param response_images
	 *            回复图片
	 * @return
	 * @throws Exception
	 */
	public String ReplyInfo(String complaint_id, String response_content, String response_images) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPost httpPost = new HttpPost(
				"https://api.mch.weixin.qq.com/v3/merchant-service/complaints-v2/" + complaint_id + "/response");
		// 请求body参数

		JSONObject dataJSON = new JSONObject();
		dataJSON.put("complainted_mchid", mchId);
		dataJSON.put("response_content", response_content);

		String[] imgs = response_images.split(",");
		JSONArray array = new JSONArray();

		for (String img : imgs) {
			array.add(img);
		}

		StringEntity entity = new StringEntity(dataJSON.toString());
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpPost);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}

	/**
	 * 反馈处理完成
	 * 
	 * @param complaint_id
	 *            投诉单号
	 * @return
	 * @throws Exception
	 */
	public String CompleteComplaints(String complaint_id) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPost httpPost = new HttpPost(
				"https://api.mch.weixin.qq.com/v3/merchant-service/complaints-v2/" + complaint_id + "/complete");
		JSONObject dataJSON = new JSONObject();
		dataJSON.put("complainted_mchid", mchId);

		StringEntity entity = new StringEntity(dataJSON.toString());
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");

		// 完成签名并执行请求
		CloseableHttpResponse response = httpClient.execute(httpPost);

		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) { // 处理成功
				result = EntityUtils.toString(response.getEntity());
			} else if (statusCode == 204) { // 处理成功，无返回Body
				result = "{'code':204}";
			} else {
				result = EntityUtils.toString(response.getEntity());
			}
		} finally {
			response.close();
		}

		return result;
	}

	/**
	 * 上传图片
	 * 
	 * @param filePath
	 *            图片路径
	 * @return
	 * @throws Exception
	 */
	public String uploadImg(String filePath) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		URI uri = new URI("https://api.mch.weixin.qq.com/v3/merchant-service/images/upload");
		File file = new File(filePath);

		try (FileInputStream ins1 = new FileInputStream(file)) {
			String sha256 = DigestUtils.sha256Hex(ins1);
			try (InputStream ins2 = new FileInputStream(file)) {
				HttpPost request = new WechatPayUploadHttpPost.Builder(uri).withImage(file.getName(), sha256, ins2)
						.build();
				CloseableHttpResponse response = httpClient.execute(request);
				try {
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode == 200) { // 处理成功
						result = EntityUtils.toString(response.getEntity());
					} else if (statusCode == 204) { // 处理成功，无返回Body
						result = "{'code':204}";
					} else {
						result = EntityUtils.toString(response.getEntity());
					}
				} finally {
					response.close();
				}

			}
		}

		return result;
	}

}

```
还有一个解密的封装类：

```java
package com.mx.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtil {
	static final int KEY_LENGTH_BYTE = 32;
	static final int TAG_LENGTH_BIT = 128;
	private final byte[] aesKey;

	/**
	 * 创建解密类
	 * 
	 * @param key
	 */
	public AesUtil(byte[] key) {
		if (key.length != KEY_LENGTH_BYTE) {
			throw new IllegalArgumentException("无效的ApiV3Key，长度必须为32个字节");
		}
		this.aesKey = key;

	}

	/**
	 * 解密数据
	 * 
	 * @param associatedData
	 *            附加数据包
	 * @param nonce
	 *            加密使用的随机串初始化向量
	 * @param ciphertext
	 *            Base64编码后的密文
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public String decryptToString(byte[] associatedData, byte[] nonce, String ciphertext)
			throws GeneralSecurityException, IOException {
		try {
			Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

			SecretKeySpec key = new SecretKeySpec(aesKey, "AES");
			GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);

			cipher.init(Cipher.DECRYPT_MODE, key, spec);
			cipher.updateAAD(associatedData);

			return new String(cipher.doFinal(Base64.getDecoder().decode(ciphertext)), "utf-8");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
```


上面这个就是楼主封装好的API，简单易用，下面是怎么调用：

```java
public static void main(String[] args) {

		WeChatAPIV3 apiv3 = new WeChatAPIV3();

		try {

			apiv3.setMchId("商户号");
			apiv3.setMchSerialNo("证书序列号");
			apiv3.setApiV3Key("APIV3秘钥");
			apiv3.setPrivateKeyFilePath("证书私钥路径");

			apiv3.setup();

			// 查询投诉列表
			// System.out.print(apiv3.GetComplaintsList(从第几个开始, 返回多少条数据,
			// "开始日期格式：yyyy-MM-dd","结束日期格式：yyyy-MM-dd"));

			// 查询投诉详情
			// System.out.print(apiv3.GetComplaintsInfo("投诉单号"));

			// 查询投诉历史
			// System.out.print(apiv3.GetComplaintsHis("投诉单号", 从第几个开始,
			// 返回多少条数据));

			// 创建投诉回调通知
			// System.out.print(apiv3.CreateComplaintsNotify("通知地址"));

			// 更新投诉回调通知
			// System.out.print(apiv3.UpdateComplaintsNotify("通知地址"));

			// 解密电话
			// System.out.print(apiv3.rsaDecryptOAEP(
			// "密文"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
```
你就只用填写对应的参数后，调用上面的方法即可，是不是一下就简单了很多，哇，你们不知道，我当时憋了好久才把这个给憋通，-=- ，主要怪楼主自己没有认真看API文档

