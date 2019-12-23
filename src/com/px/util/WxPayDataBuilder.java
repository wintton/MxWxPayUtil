package com.px.util;

import net.sf.json.JSONObject;

interface WxPayDataBuilder {
	boolean build() throws LackParamExceptions;

	JSONObject hand() throws LackParamExceptions;
}
