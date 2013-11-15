package com.lyricoo;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Use LyricooResponseAdapter to convert reliably from 
 * AsyncHttpResponse to JsonHttpResponse.
 * 
 * Usage LyricooResponseAdapter.adapt(asyncJsonHttpResponseHandler);
 * @author 
 *
 */
public class LyricooResponseAdapter {
	/**
	 * Converts byte array to object form
	 * @param in byte[]
	 * @return Object
	 */
	private static Object toJson(byte[] in) {
		Object json = null;
		try {
			String decoded = new String(in, "UTF-8");
			json = new JSONTokener(decoded).nextValue();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return json;
	}
	
	/**
	 * Adapter that converts http responses to json responses
	 * @param JsonHttpResponseHandler handles json responses
	 * @return AsyncHttpResponseHandler
	 */
	public static AsyncHttpResponseHandler adapt(final JsonHttpResponseHandler responseHandler) {
		return new AsyncHttpResponseHandler() {
			@Override
		     public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Object json = LyricooResponseAdapter.toJson(responseBody);
				if (json instanceof JSONObject) {
					responseHandler.onSuccess((JSONObject) json);
				} else if (json instanceof JSONArray) {
					responseHandler.onSuccess((JSONArray) json);
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, java.lang.Throwable error) {
				Object json = LyricooResponseAdapter.toJson(responseBody);
				if (json instanceof JSONObject) {
					responseHandler.onFailure(error, (JSONObject) json);
				} else if (json instanceof JSONArray) {
					responseHandler.onFailure(error, (JSONArray) json);
				}
			}
			
			@Override
			public void onStart() {
				responseHandler.onStart();
			}
			
			@Override
			public void onFinish() {
				responseHandler.onFinish();
			}
			
			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				responseHandler.onProgress(bytesWritten, totalSize);
			}
			
			@Override
			public void onRetry() {
				responseHandler.onRetry();
			}
			
		};
	}
}
