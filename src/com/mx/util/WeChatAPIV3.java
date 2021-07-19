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
	String mchId = "";
	String mchSerialNo = "";
	String apiV3Key = "";
	String privateKeyFilePath = "";

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

	public static void main(String[] args) {

		WeChatAPIV3 apiv3 = new WeChatAPIV3();

		apiv3.setMchId("商户号");
		apiv3.setMchSerialNo("证书序列号");
		apiv3.setApiV3Key("APIV3秘钥");
		apiv3.setPrivateKeyFilePath("证书私钥路径");

		try {

			apiv3.setup();

			// 查询投诉列表
			// System.out.print(apiv3.GetComplaintsList(0, 10,
			// "2021-07-01","2021-07-16"));

			// 查询投诉详情
			// System.out.print(apiv3.GetComplaintsInfo("投诉单号"));

			// 查询投诉历史
			// System.out.print(apiv3.GetComplaintsHis("投诉单号", 0,
			// 10));

			// 创建投诉回调通知
			// System.out.print(apiv3.CreateComplaintsNotify("回调地址"));

			// 更新投诉回调通知
			// System.out.print(apiv3.UpdateComplaintsNotify("回调地址"));

			// 解密电话
			// System.out.print(apiv3.rsaDecryptOAEP(
			// "密文"));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
