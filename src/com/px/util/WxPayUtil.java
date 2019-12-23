package com.px.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class WxPayUtil {
	private static SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

	public static String getRandomStr(int length) {
		String chars = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ System.currentTimeMillis();/**/
		int maxPos = chars.length();
		String[] charsMore = chars.split("");
		StringBuffer noceStr = new StringBuffer();

		for (int i = 0; i < length; i++) {
			noceStr.append(charsMore[(int) Math.floor(Math.random() * maxPos)]);
		}
		return noceStr.toString(); // 随机数
	}

	public static String MD5(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			byte[] bytes = messageDigest.digest(str.getBytes("utf-8"));
			return byteArrayToHexString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f" };

	private static String byteToHexString(byte b) {

		int n = b;
		if (n < 0) {
			n += 256;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static Map<String, String> xmlToMap(String xml) {
		try {
			Map<String, String> data = new HashMap<>();
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
			org.w3c.dom.Document doc = documentBuilder.parse(stream);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getDocumentElement().getChildNodes();
			for (int idx = 0; idx < nodeList.getLength(); ++idx) {
				Node node = nodeList.item(idx);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					org.w3c.dom.Element element = (org.w3c.dom.Element) node;
					data.put(element.getNodeName(), element.getTextContent());
				}
			}
			stream.close();
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String byteArrayToHexString(byte[] digest) {
		StringBuffer result = new StringBuffer();
		for (byte b : digest) {
			result.append(byteToHexString(b));
		}
		return result.toString();
	}

	public static String FormatDate(long timeinmill) {
		return sDateFormat.format(new Date(timeinmill));
	}

	public static JSONObject XmlParseJSON(String xml) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		String resutStr = xmlSerializer.read(xml).toString();
		JSONObject result = JSONObject.fromObject(resutStr);

		return result;
	}

	public static String sendHttpsRequest(String url, String data, String sendType, String contentType, String methed)
			throws IOException {

		if ("GET".equals(methed)) {
			url = url.indexOf("?") >= 0 ? url + data : url + "?" + data;
		}
		if (sendType == null) {
			sendType = "text/xml";
		}
		if (contentType == null) {
			contentType = "utf-8";
		}

		URL requestUrl = new URL(url);
		HttpsURLConnection connection = (HttpsURLConnection) requestUrl.openConnection();
		connection.setDoInput(true); // 允许输入流，即允许下�?
		connection.setDoOutput(true); // 允许输出流，即允许上�?
		connection.setUseCaches(false); // 不使用缓�?
		connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

		if ("POST".equals(methed)) {
			connection.setRequestProperty("Content-type", sendType);
			connection.setRequestProperty("Cache-Control", "no-cache");
			connection.setRequestMethod("POST");

			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

			out.write(new String(data.getBytes(contentType)));
			out.flush();
			out.close();
		} else {
			connection.setRequestMethod("GET"); // 使用get请求
		}

		InputStream is = connection.getInputStream(); // 获取输入流，此时才真正建立链�?

		StringBuffer stringBuffer = new StringBuffer();
		InputStreamReader isr = new InputStreamReader(is, "utf-8");
		BufferedReader bufferReader = new BufferedReader(isr);
		String inputLine = "";

		while ((inputLine = bufferReader.readLine()) != null) {
			stringBuffer.append(inputLine);
		}

		is.close();
		connection.disconnect();
		return stringBuffer.toString();
	}

	public static String doPostDataWithCert(String url, String data, String mch_id, String filPath) throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File(filPath));// P12文件目录

		try {
			keyStore.load(instream, mch_id.toCharArray());
		} finally {
			instream.close();
		}

		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mch_id.toCharArray())// 这里也是写密码的
				.build();

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		try {
			HttpPost httpost = new HttpPost(url); // 设置响应头信息
			httpost.addHeader("Connection", "keep-alive");
			httpost.addHeader("Accept", "*/*");
			httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			httpost.addHeader("Host", "api.mch.weixin.qq.com");
			httpost.addHeader("X-Requested-With", "XMLHttpRequest");
			httpost.addHeader("Cache-Control", "max-age=0");
			httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
			httpost.setEntity(new StringEntity(data, "UTF-8"));
			CloseableHttpResponse response = httpclient.execute(httpost);
			try {
				HttpEntity entity = response.getEntity();

				String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
				EntityUtils.consume(entity);
				return jsonStr;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}
}
