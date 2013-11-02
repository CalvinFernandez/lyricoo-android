package com.lyricoo;

import android.util.Log;

import com.loopj.android.http.*;

/**
 * This class provides easy methods for get and post requests to our api.
 * Implementation provided by Loopj Android Asynchronous Http Client See:
 * http://loopj.com/android-async-http/ for more documentation
 * 
 * Usage: LyricooAPI.get("users/all", params, responseHandler)
 * 
 * For params: RequestParams params = new RequestParams();
 * params.put("username", "james"); 
 * http://loopj.com/android-async-http/doc/com/loopj/android/http/RequestParams.html
 * 
 * For response handler: new JsonHttpResponseHandler() {
 * 
 * @Override public void onSuccess(JSONObject json)
 * @Override public void onFailure(java.lang.String responseBody,
 *           java.lang.Throwable error) }); * 
 * 
 *           http://loopj.com/android-async-http/doc/com/loopj/android/http/
 *           AsyncHttpResponseHandler.html
 * 
 */
public class LyricooAPI {
	public static final String BASE_URL = "http://lyricoo.com:8080";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + "/" + relativeUrl;
	}

}
