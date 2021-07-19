package com.mx.util;

import net.sf.json.JSONObject;

/**
 * v1.0.2
 * 
 * @author 周工 2020-06-01
 */
interface WxPayDataBuilder {
	boolean build() throws LackParamExceptions;

	JSONObject hand() throws LackParamExceptions;
}
