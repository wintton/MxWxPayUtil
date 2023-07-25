package com.mx.util;

import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.WechatPayUploadHttpPost;
import com.wechat.pay.contrib.apache.httpclient.auth.*;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;

public class WeChatAPIV3 {

	CloseableHttpClient httpClient;

	CloseableHttpClient httpNoSignPayClient;

	String mchId = "";
	String mchSerialNo = "";
	String apiV3Key = "";
	String privateKeyFilePath = "";

	String privateKey = "";
	String certKeyFilePath = "";
	PrivateKey merchantPrivateKey;
	PublicKey merchantPublicKey;
	PublicKey pingtaiPublicKey;
	String errorHint = "";

	public String getErrorHint() {
		return errorHint;
	}

	public void setErrorHint(String errorHint) {
		this.errorHint = errorHint;
	}

	public boolean isStep() {
		return isStep;
	}

	long lastUseTime = 0;

	public long getLastUseTime() {
		return lastUseTime;
	}

	public void setLastUseTime(long lastUseTime) {
		this.lastUseTime = lastUseTime;
	}

	public String getCertKeyFilePath() {
		return certKeyFilePath;
	}

	public void setCertKeyFilePath(String certKeyFilePath) {
		this.certKeyFilePath = certKeyFilePath;
	}

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

	public void loadPTCert(String certKey) throws UnsupportedEncodingException {
		pingtaiPublicKey = PemUtil.loadCertificate(new ByteArrayInputStream(certKey.getBytes("utf-8"))).getPublicKey();
	}

	public String rsaEncryptOAEP_PT(String message) throws IllegalBlockSizeException, IOException {

		if (pingtaiPublicKey == null) {
			errorHint = "未成功加载公钥";
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pingtaiPublicKey);

			byte[] data = message.getBytes("utf-8");
			byte[] cipherdata = cipher.doFinal(data);
			return Base64.getEncoder().encodeToString(cipherdata);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("无效的证书", e);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
		}
	}

	public String signBySHA256WithRSA(String content) {
		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		if (merchantPrivateKey == null) {
			errorHint = "未成功加载私钥";
			return null;
		}

		try {
			// PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
			// org.apache.commons.codec.binary.Base64.decodeBase64(privateKey));
			// PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(priPKCS8);

			Signature signature = Signature.getInstance("SHA256withRSA");
			signature.initSign(merchantPrivateKey);
			signature.update(content.getBytes("utf-8"));

			return org.apache.commons.codec.binary.Base64.encodeBase64String(signature.sign());
		} catch (Exception e) {
			// 签名失败
			e.printStackTrace();
			return null;
		}
	}

	public String rsaEncryptOAEP(String message) throws IllegalBlockSizeException, IOException {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		if (merchantPublicKey == null) {
			errorHint = "未成功加载公钥";
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, merchantPublicKey);

			byte[] data = message.getBytes("utf-8");
			byte[] cipherdata = cipher.doFinal(data);
			return Base64.getEncoder().encodeToString(cipherdata);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException("当前Java环境不支持RSA v1.5/OAEP", e);
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("无效的证书", e);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalBlockSizeException("加密原串的长度不能超过214字节");
		}
	}

	public boolean doCheckParam() {

		return doCheckValue(mchId, mchSerialNo, apiV3Key, privateKeyFilePath, certKeyFilePath);
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

			extracted();
//			extractedNew();

			isStep = true;

		} catch (Exception e) {
			errorHint = errorHint.toString();
			isStep = false;
			e.printStackTrace();
		}

	}

	private void extractedNew() throws Exception {

		String certKey = new String(Files.readAllBytes(Paths.get(certKeyFilePath)), "utf-8");

		merchantPublicKey = PemUtil.loadCertificate(new ByteArrayInputStream(certKey.getBytes("utf-8")))
				.getPublicKey();

		privateKey = new String(Files.readAllBytes(Paths.get(privateKeyFilePath)), "utf-8");

		merchantPrivateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes("utf-8")));


		CertificatesManager certificatesManager = CertificatesManager.getInstance();
		// 向证书管理器增加需要自动更新平台证书的商户信息
		certificatesManager.putMerchant(mchId, new WechatPay2Credentials(mchId,
				new PrivateKeySigner(mchSerialNo, merchantPrivateKey)), apiV3Key.getBytes(StandardCharsets.UTF_8));
		// ... 若有多个商户号，可继续调用putMerchant添加商户信息

		// 从证书管理器中获取verifier
		Verifier verifier = certificatesManager.getVerifier(mchId);
		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
				.withMerchant(mchId, mchSerialNo, merchantPrivateKey)
				.withValidator(new WechatPay2Validator(verifier));
		// ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient

		// 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
		httpClient = builder.build();
	}

	@Deprecated
	private void extracted() throws IOException {
		privateKey = new String(Files.readAllBytes(Paths.get(privateKeyFilePath)), "utf-8");

		String certKey = new String(Files.readAllBytes(Paths.get(certKeyFilePath)), "utf-8");

		// 加载商户私钥（privateKey：私钥字符串）
		merchantPrivateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(privateKey.getBytes("utf-8")));

		// 加载商户公钥（privateKey：证书字符串）
		merchantPublicKey = PemUtil.loadCertificate(new ByteArrayInputStream(certKey.getBytes("utf-8")))
				.getPublicKey();

		// 加载平台证书（mchId：商户号,mchSerialNo：商户证书序列号,apiV3Key：V3密钥）
		AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
				new WechatPay2Credentials(mchId, new PrivateKeySigner(mchSerialNo, merchantPrivateKey)),
				apiV3Key.getBytes("utf-8"));


		// 初始化httpClient
		httpClient = WechatPayHttpClientBuilder.create().withMerchant(mchId, mchSerialNo, merchantPrivateKey)
				.withValidator(new WechatPay2Validator(verifier)).build();

		WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
				.withMerchant(mchId, mchSerialNo, merchantPrivateKey)
				//设置响应对象无需签名
				.withValidator((response) -> true);

		httpNoSignPayClient = builder.build();


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
	 * 查询通知回调地址
	 * @param
	 * @return
	 * @throws Exception
	 */
	public String queryComplaintsNotify() throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/merchant-service/complaint-notifications");
		// 请求URL
		HttpGet httpGet = new HttpGet(uriBuilder.build());
		httpGet.addHeader("Accept", "application/json");


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
	 * @param responseJson
	 *            回复内容
	 * @return
	 * @throws Exception
	 */
	public String ReplyInfo(String complaint_id, JSONObject responseJson) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPost httpPost = new HttpPost(
				"https://api.mch.weixin.qq.com/v3/merchant-service/complaints-v2/" + complaint_id + "/response");
		// 请求body参数

		responseJson.put("complainted_mchid", mchId);

		StringEntity entity = new StringEntity(responseJson.toString(),"UTF-8");
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

	/**
	 * 下载图片
	 *
	 * @param media_url
	 *            图片路径
	 * @return
	 * @throws Exception
	 */
	public BufferedImage downLoadImg(String media_url, String filePath) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		HttpGet httpGet = new HttpGet(media_url);
		httpGet.setHeader("Accept", "application/json");

		CloseableHttpResponse response = httpNoSignPayClient.execute(httpGet);

		BufferedImage result = null;

		try {
			HttpEntity responseEntity = response.getEntity();
			InputStream content = responseEntity.getContent();
			result = ImageIO.read(content);
			if(filePath != null && filePath.length() >= 0){
				File file = new File(filePath);
				if(!file.exists()){
					file.createNewFile();
				}
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				int bytesWritten = 0;
				int byteCount = 0;
				byte[] bytes = new byte[1024];
				while ((byteCount = content.read(bytes)) != -1)
				{
					fileOutputStream.write(bytes, bytesWritten, byteCount);
					bytesWritten += byteCount;
				}
				content.close();
				fileOutputStream.close();
			}
		} finally {
			response.close();
		}

		return result;
	}

	/**
	 * 发送请求
	 *
	 * @param url
	 *            发送地址
	 * @return
	 * @throws Exception
	 */
	public String doSendPostUrl(String url, String sendcontent) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPost httpPost = new HttpPost(url);

		StringEntity entity = new StringEntity(sendcontent, "utf-8");
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

	public String doSendPostUrl(String url, String sendcontent, String Wechatpay_Serial) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpPost httpPost = new HttpPost(url);

		StringEntity entity = new StringEntity(sendcontent, "utf-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		httpPost.setHeader("Accept", "application/json");
		httpPost.addHeader("Wechatpay-Serial", Wechatpay_Serial);

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
	 * 发送请求
	 *
	 * @param url
	 *            发送地址
	 * @return
	 * @throws Exception
	 */
	public String doSendGETUrl(String url) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		// 请求URL
		HttpGet httpGet = new HttpGet(url);
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
	 * 发送请求
	 *
	 * @param url
	 *            发送地址
	 * @return
	 * @throws Exception
	 */
	public String doSendPostImages(String url, String filepath) throws Exception {

		if (!isStep) {
			errorHint = "未成功启用Step";
			return null;
		}

		String result = null;

		File file = new File(filepath);
		URI uri = new URI(url);

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
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					response.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

}

