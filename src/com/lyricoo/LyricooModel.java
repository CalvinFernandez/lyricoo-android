package com.lyricoo;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LyricooModel {

	private String baseUrl;
	
	LyricooModel(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	LyricooModel() {
		this("");
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public void get(AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.get(baseUrl, new RequestParams(), responseHandler);
	}
	
	public void get(RequestParams params, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.get(baseUrl, params, responseHandler);
	}
	
	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void get(String url, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.get(getAbsoluteUrl(url), new RequestParams(), responseHandler);
	}
	
	public void post(RequestParams params, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.post(baseUrl, params, responseHandler);
	}
	
	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.post(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void put(RequestParams params, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.put(baseUrl, params, responseHandler);
	}
	
	public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.put(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void delete(AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.delete(baseUrl, responseHandler);
	}
	
	public void delete(String url, AsyncHttpResponseHandler responseHandler) {
		LyricooAPI.delete(getAbsoluteUrl(url), responseHandler);
	}
	
	private String getAbsoluteUrl(String relativeUrl) {
		return baseUrl + "/" + relativeUrl;
	}
	
}
