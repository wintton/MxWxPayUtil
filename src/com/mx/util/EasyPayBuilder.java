package com.mx.util;

import java.io.IOException;

import net.sf.json.JSONObject;

public class EasyPayBuilder implements WxPayDataBuilder {

	private static String sendUrl = "http://pay2.w-x.net.cn/Pay/PlaceOrder";
	private static String sendUrl2 = "http://api.sczsgc.cn/Pay/PlaceOrder";

	private String notify_url, return_url, orderid, orderuid, goodsname, key, token;

	private int uid, istype;

	private float price;

	private StringBuffer reStringBuffer;
	private StringBuffer signStringBuffer;
	private boolean build = false;

	public EasyPayBuilder() {
		orderid = System.currentTimeMillis() + WxPayUtil.getRandomStr(8);
		orderuid = System.currentTimeMillis() + WxPayUtil.getRandomStr(8);
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getReturn_url() {
		return return_url;
	}

	public void setReturn_url(String return_url) {
		this.return_url = return_url;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getOrderuid() {
		return orderuid;
	}

	public void setOrderuid(String orderuid) {
		this.orderuid = orderuid;
	}

	public String getGoodsname() {
		return goodsname;
	}

	public void setGoodsname(String goodsname) {
		this.goodsname = goodsname;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getIstype() {
		return istype;
	}

	public void setIstype(int istype) {
		this.istype = istype;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	@Override
	public boolean build() throws LackParamExceptions {

		build = false;
		reStringBuffer = new StringBuffer();
		signStringBuffer = new StringBuffer();

		appendParam("goodsname", goodsname, true);
		appendParam("istype", istype + "", true);
		appendParam("notify_url", notify_url, true);
		appendParam("orderid", orderid, true);
		appendParam("orderuid", orderuid, false);
		appendParam("price", price + "", true);
		appendParam("return_url", return_url, true);

		if (token == null) {
			throw new LackParamExceptions("参数token不能为空");
		}

		signStringBuffer.append(token);

		appendParam("uid", uid + "", true);

		key = WxPayUtil.MD5(signStringBuffer.toString());

		appendParam("key", key, true);

		build = true;

		System.out.println(reStringBuffer.toString());

		return build;
	}

	@Override
	public JSONObject hand() throws LackParamExceptions {
		if (!build) {
			throw new LackParamExceptions("未build成功，请先确认build成功后再运行");
		}
		String result = "";
		try {
			// 正常的域名
			result = WxPayUtil.sendHttpRequest(sendUrl, reStringBuffer.toString(), "application/x-www-form-urlencoded",
					"utf-8", "POST");
		} catch (IOException e) {
			e.printStackTrace();
			// 备用域名
			try {
				result = WxPayUtil.sendHttpRequest(sendUrl2, reStringBuffer.toString(),
						"application/x-www-form-urlencoded", "utf-8", "POST");
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new LackParamExceptions(e1.toString());
			}
		}
		System.out.println(result);
		return null;
	}

	private void appendParam(String key, String value, boolean isneed) throws LackParamExceptions {

		if (value == null) {
			if (isneed) {
				throw new LackParamExceptions("参数" + key + "不能为空");
			} else {
				return;
			}
		}

		this.reStringBuffer.append(key + "=" + value + "&");
		this.signStringBuffer.append(value);
	}

}
